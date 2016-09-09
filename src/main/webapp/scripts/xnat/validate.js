/*!
 * Form and value validation functions for XNAT
 * Some code adapted from http://rickharrison.github.com/validate.js
 */

var XNAT = getObject(XNAT);

(function(factory){
    if (typeof define === 'function' && define.amd) {
        define(factory);
    }
    else if (typeof exports === 'object') {
        module.exports = factory();
    }
    else {
        return factory();
    }
}(function(){

    var undefined, validate;

    XNAT.validation = getObject(XNAT.validation || {});

    // HELPERS

    function isNull(value){
        return !!(value == null || value === '');
    }

    // copy of jQuery's $.isNumeric() method
    function isNumeric( num ) {
        return !isArray( num ) && (num - parseFloat( num ) + 1) >= 0;
    }

    // TODO: implement display of error messages
    var message = {
        required: 'The __NAME__ field is required.',
        matches: 'The __NAME__ field does not match the __VALUE__ field.',
        "default": 'The __NAME__ field is still set to default, please change.',
        email: 'The __NAME__ field must contain a valid email address.',
        emails: 'The __NAME__ field must contain all valid email addresses.',
        minLength: 'The __NAME__ field must be at least __VALUE__ characters in length.',
        maxLength: 'The __NAME__ field must not exceed __VALUE__ characters in length.',
        exactLength: 'The __NAME__ field must be exactly __VALUE__ characters in length.',
        greaterThan: 'The __NAME__ field must contain a number greater than __VALUE__.',
        lessThan: 'The __NAME__ field must contain a number less than __VALUE__.',
        alpha: 'The __NAME__ field must only contain alphabetical characters.',
        alphaNumeric: 'The __NAME__ field must only contain alpha-numeric characters.',
        alphaDash: 'The __NAME__ field must only contain alpha-numeric characters, underscores, and dashes.',
        numeric: 'The __NAME__ field must contain only numbers.',
        number: "The __NAME__ value must be of type 'number'.",
        integer: 'The __NAME__ field must contain an integer.',
        decimal: 'The __NAME__ field must contain a decimal number.',
        natural: 'The __NAME__ field must contain only positive numbers.',
        naturalNoZero: 'The __NAME__ field must contain a number greater than zero.',
        ip: 'The __NAME__ field must contain a valid IP.',
        base64: 'The __NAME__ field must contain a base64 string.',
        creditCard: 'The __NAME__ field must contain a valid credit card number.',
        fileType: 'The __NAME__ field must contain only __VALUE__ files.',
        validUrl: 'The __NAME__ field must contain a valid URL.',
        greaterThanDate: 'The __NAME__ field must contain a more recent date than __VALUE__.',
        lessThanDate: 'The __NAME__ field must contain an older date than __VALUE__.',
        greaterThanOrEqualDate: "The __NAME__ field must contain a date that's at least as recent as __VALUE__.",
        lessThanOrEqualDate: "The __NAME__ field must contain a date that's __VALUE__ or older."
    };

    // auto-generate alternate property names from camelCase names
    // creates hyphen-ated and under_score aliases
    // clutters up the namespace, but... oh, well
    forOwn(message, function(name){
        message[name.toLowerCase()] = message[name];  // lowercase names
        message[toDashed(name)]     = message[name];  // hyphen-ated names
        message[toUnderscore(name)] = message[name];  // under_score names
    });


    var regex = {
        required: /[\S\W]+/,                // whitespace characters will still validate
        notEmpty: /[\S]/,                   // must contain more than just whitespace characters
        rule: /^(.+?)\[(.+)\]$/,            // ?
        //numeric: /^-?\d*\d{3}[,]*\d[.]*\d+$/,
        integer: /^-?[0-9]+$/,              // positive or negative whole number
        natural: /^[0-9]+$/,                // positive whole number
        naturalNoZero: /^[1-9][0-9]*$/,     // no leading 0
        decimal: /^-?[0-9]*\.?[0-9]+$/,
        hexadecimal: /^[0-9a-f]+$/,
        email: /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
        alpha: /^[a-z]+$/i,                 // ONLY letters
        alphaSafe: /^[a-z_]+$/i,            // ONLY letters and underscores
        alphaDash: /^[a-z_\-]+$/i,          // ONLY letters, underscore, and dash
        alphaNum: /^[a-z0-9]+$/i,           // ONLY letters and numbers
        alphaNumSafe: /^[a-z0-9_]+$/i,      // ONLY letters, numbers, and underscore
        alphaNumDash: /^[a-z0-9_\-]+$/i,    // ONLY letters, numbers, underscore, and dash
        alphaNumDashSpace: /^[a-z0-9_\- ]+$/i, // ONLY letters, numbers, underscore, dash, and space
        idSafe: /^[a-z][a-z0-9_\-]+$/i,     // safe to use as an ID - alphasafe and must start with a letter
        idStrict: /^[a-z][a-z0-9_]+$/i,     // 'idSafe' without hyphens
        ip: /^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$/i,
        base64: /[^a-zA-Z0-9\/+=]/i,
        numericDash: /^[\d\-\s]+$/,
        url: /^((http|https):\/\/(\w+:{0,1}\w*@)?(\S+)|)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/,
        uri: /^([\/](\w+:{0,1}\w*@)?(\S+)|)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/,
        // these date regexes can't check leap years or other incorrect MM/DD combos
        dateISO: /^(19|20)\d\d([- /.])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$/,
        dateUS: /^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\d\d$/,
        dateEU: /^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d$/,
        // CRON!!!!!  (Say it like "KHAN!!!!!")
        cronWords: /^@(reboot|yearly|annually|monthly|weekly|daily|midnight|hourly)$/i,
        cronSeconds: /^((\*|\?|0|([1-9]|[1-5][0-9]))(\/\d+)?)$/,
        cronMinutes: /^((\*|\?|0|([1-9]|[1-5][0-9]))(\/\d+)?)$/,
        cronHours: /^((\*|\?|([0-9]|1[0-9]|2[0-3]))(\/\d+)?)$/,
        cronDay: /^((\*|\?|([0-9]|[1-2][0-9]|3[0-1]))(\/\d+)?)$/,
        cronMonth: /^((\*|\?|([0-9]|1[0-2]))(\/\d+)?)$/,
        cronMonths: /^(((\*|\?|([0-9]|1[0-2]))(\/\d+)?)|(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|DEC))$/i,
        cronMonthNames: /^(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|DEC)$/i,
        cronWeekday: /^((\*|\?|([0-7]))(\/\d+)?)$/,
        cronWeekdays: /^(((\*|\?|([0-7]))(\/\d+)?)|(MON|TUE|WED|THU|FRI|SAT|SUN))$/i,
        cronWeekdayNames: /^(MON|TUE|WED|THU|FRI|SAT|SUN)$/i,
        // cronAlt: /^(\*|([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])|\*\/([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])) (\*|([0-9]|1[0-9]|2[0-3])|\*\/([0-9]|1[0-9]|2[0-3])) (\*|([1-9]|1[0-9]|2[0-9]|3[0-1])|\*\/([1-9]|1[0-9]|2[0-9]|3[0-1])) (\*|([1-9]|1[0-2])|\*\/([1-9]|1[0-2])) (\*|([0-6])|\*\/([0-6]))$/,
        // cron regex lifted from this post: http://stackoverflow.com/questions/235504/validating-crontab-entries-w-php
        // cron: /^\s*($|#|\w+\s*=|(\*(?:\/\d+)?|(?:[0-5]?\d)(?:-(?:[0-5]?\d)(?:\/\d+)?)?(?:,(?:[0-5]?\d)(?:-(?:[0-5]?\d)(?:\/\d+)?)?)*)\s+(\*(?:\/\d+)?|(?:[01]?\d|2[0-3])(?:-(?:[01]?\d|2[0-3])(?:\/\d+)?)?(?:,(?:[01]?\d|2[0-3])(?:-(?:[01]?\d|2[0-3])(?:\/\d+)?)?)*)\s+(\*(?:\/\d+)?|(?:0?[1-9]|[12]\d|3[01])(?:-(?:0?[1-9]|[12]\d|3[01])(?:\/\d+)?)?(?:,(?:0?[1-9]|[12]\d|3[01])(?:-(?:0?[1-9]|[12]\d|3[01])(?:\/\d+)?)?)*)\s+(\*(?:\/\d+)?|(?:[1-9]|1[012])(?:-(?:[1-9]|1[012])(?:\/\d+)?)?(?:,(?:[1-9]|1[012])(?:-(?:[1-9]|1[012])(?:\/\d+)?)?)*|jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\s+(\*(?:\/\d+)?|(?:[0-6])(?:-(?:[0-6])(?:\/\d+)?)?(?:,(?:[0-6])(?:-(?:[0-6])(?:\/\d+)?)?)*|mon|tue|wed|thu|fri|sat|sun)\s+|(@reboot|@yearly|@annually|@monthly|@weekly|@daily|@midnight|@hourly)\s+)([^\s]+)\s+(.*)$/,
        bogus: /bogus/i // filler
    };
    // aliases
    regex.int = regex.integer;
    regex.number = regex.numeric;
    regex.float = regex.decimal;
    regex.hex = regex.hexadecimal;
    regex.alphaNumeric = regex.alphaNum;
    regex.alphaNumericSafe = regex.alphaNumSafe;
    regex.ipAddr = regex.ipAddress = regex.ip;
    regex.fullUrl = regex.url;
    regex.date = regex.dateISO;

    // auto-generate alternate property names from camelCase names
    // creates hyphen-ated and under_score aliases
    // clutters up the namespace, but... oh, well
    forOwn(regex, function(name){
        regex[name.toLowerCase()] = regex[name];  // lowercase names
        regex[toDashed(name)]     = regex[name];  // hyphen-ated names
        regex[toUnderscore(name)] = regex[name];  // under_score names
    });

    // export combined 'regex' object back to global namespace
    XNAT.validation.regex = extend(regex, XNAT.validation.regex || {});


    // define custom test methods for more complex validations
    var test = {};

    test.numeric = function(value){
        console.log('numeric');
        return isNumeric(value);
    };

    test.interval = function(value){
        console.log('interval');
        var parts = value.split(/([0-9]+)/);
        var units = /\s+(sec|second|min|minute|hour|day|week|month|year)(s)?\s*/;
        var num = true;
        var valid = true;
        var i = 1; // start i at 1 since parts[0] will be an empty string
        var part;
        while (parts[i] && valid === true) {
            part = (parts[i] + '');
            if (num) {
                valid = /\d+/.test(part);
            }
            else {
                valid = units.test(part);
            }
            num = !num; // flip for next iteration
            i++;
        }
        return valid;
    };

    // match to 6-field cron syntax:
    // 0 0 * * * *
    test.cronFn = function(value){

        // TODO: replace all this with regex tests

        /*
        var WORDS = ('reboot yearly annually monthly ' +
            'weekly daily midnight hourly').split(/\s+/).map(function(word){
            return '@' + word
        });
        var MONTHS = ('JAN FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC').split(/\s+/);
        var DAYS = ('MON TUE WED THU FRI SAT SUN').split(/\s+/);

        // is it a special cron keyword?
        if (WORDS.indexOf(value) > -1) {
            return true
        }

        // split passed value into separate fields
        var FIELDS = (value+'').trim().split(/\s+/);

        // check for 6 fields
        if (FIELDS.length < 6) {
            return false
        }

        var SECONDS = FIELDS[0].split('/');
        var MINUTES = FIELDS[1].split('/');
        var HOURS   = FIELDS[2].split('/');
        var DAY     = FIELDS[3];
        var MONTH   = FIELDS[4];
        var WEEKDAY = FIELDS[5];

        var errors = 0;

        function isWild(val){
            return /[*?/]/.test((val+'').trim());
        }

        function isRange(val){
            return /[a-z0-9]-[a-z0-9]/i.test(val)
        }

        function isError(val, regex){
            var notWild = !isWild(val);
            var notMatch = regex ? !regex.test(val) : false;
            if (notWild && notMatch) {
                errors++
            }
        }

        function checkTime(val, limit, regex){

            val = [].concat(val);

            var notWild = !isWild(val[0]);

            if (notWild){
                if (+val[0] < 0 || +val[0] > limit) {
                    isError(val, regex)
                }
            }

            // seconds interval must be a number
            if (val[1] && !/[0-9]/.test(val[1])) {
                errors++
            }

        }

        // seconds
        checkTime(SECONDS, 59);

        // minutes
        checkTime(MINUTES, 59);

        // hours
        checkTime(HOURS, 23);

        // day
        checkTime(DAY, 31);

        // month
        var monthRegex = /JAN|FEB|MAR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC/i;
        checkTime(MONTH, 12, monthRegex);

        // day of the week
        var weekdayRegex = /MON|TUE|WED|THU|FRI|SAT|SUN/i;
        checkTime(WEEKDAY, 7, weekdayRegex);

        return (errors === 0);

        */

    };

    // check a comma- or space-separated list of multiple email addresses
    test.emails = function(value){
        var errors = 0;
        value.split(/[,\s]+/).forEach(function(email){
            if (errors) return false;
            email = email.trim();
            if (!regex.email.test(email)) {
                errors++
            }
        });
        return errors === 0;
    };

    // make sure there's a minimum number of characters
    test.minLength = function(value, length){
        if (!regex.naturalNoZero.test(length)) {
            return false;
        }
        return (value.length >= parseInt(length, 10));
    };

    // don't exceed the maximum number of characters
    test.maxLength = function(value, length){
        if (!regex.naturalNoZero.test(length)) {
            return false;
        }
        return (value.length <= parseInt(length, 10));
    };

    test.exactLength = function(value, length){
        if (!regex.naturalNoZero.test(length)) {
            return false;
        }
        return (value.length === parseInt(length, 10));
    };
    test.isLength = test.exactLength;

    // XNAT.validate('#concurrent-sessions').is('greaterThan', 0).check();
    test.greaterThan = function(value, num){
        if (!regex.decimal.test(value)) {
            return false;
        }
        return (parseFloat(value) > parseFloat(num));
    };

    test.greaterThanOrEqual = function(value, num){
        if (!regex.decimal.test(value)) {
            return false;
        }
        return (parseFloat(value) >= parseFloat(num));
    };
    test.greaterThanOrEqualTo = test.greaterThanOrEqual;
    test.gte = test.greaterThanOrEqual;

    // XNAT.validate('#session-timeout').is('lessThan', 999).check();
    test.lessThan = function(value, num){
        if (!regex.decimal.test(value)) {
            return false;
        }
        return (parseFloat(value) < parseFloat(num));
    };

    test.lessThanOrEqual = function(value, num){
        if (!regex.decimal.test(value)) {
            return false;
        }
        return (parseFloat(value) <= parseFloat(num));
    };
    test.lessThanOrEqualTo = test.lessThanOrEqual;
    test.lte = test.lessThanOrEqual;

    test.equalTo = function(value, test){
        return value+'' === test+''
    };
    test.equals = test.equalTo;

    // XNAT.validate('input.credit-card').is('creditCard').check();
    test.creditCard = function(value){
        // Luhn Check Code from https://gist.github.com/4075533
        // accept only digits, dashes or spaces
        if (!regex.numericDash.test(value)) return false;

        // The Luhn Algorithm. It's so pretty.
        var nCheck = 0, nDigit = 0, bEven = false;
        var strippedField = value.replace(/\D/g, "");

        for (var n = strippedField.length - 1; n >= 0; n--) {
            var cDigit = strippedField.charAt(n);
            nDigit = parseInt(cDigit, 10);
            if (bEven) {
                if ((nDigit *= 2) > 9) nDigit -= 9;
            }

            nCheck += nDigit;
            bEven = !bEven;
        }

        return (nCheck % 10) === 0;
    };

    // Check file extension of submitted file.
    //
    // XNAT.validate('input.doc[type="file"]').is('fileType', 'doc').check()
    //
    test.fileType = function(value, type){
        //if (type !== 'file') {
        //    return true;
        //}
        var ext       = value.substr((value.lastIndexOf('.') + 1)).toLowerCase(),
            typeArray = type.split(','),
            inArray   = false,
            i         = -1,
            len       = typeArray.length;

        while (++i < len) {
            if (ext == typeArray[i].toLowerCase()) {
                inArray = true;
            }
        }

        return inArray;
    };

    // auto-generate alternate property names from camelCase names
    // creates hyphen-ated and under_score aliases
    forOwn(test, function(name){
        test[name.toLowerCase()] = test[name];  // lowercase names
        test[toDashed(name)]     = test[name];  // hyphen-ated names
        test[toUnderscore(name)] = test[name];  // under_score names
    });

    // export combined 'test' object back to global namespace
    XNAT.validation.test = extend(test, XNAT.validation.test || {});


    // HELPERS

    function init(element){
        var obj = {
            element$: $.spawn('input.tmp|type=hidden'),
            len: 0,
            regex: '',
            value: '',
            values: [], // use to check more than one value
            validated: null
        };
        obj.element = obj.element$[0];
        if (element) {
            obj.element$ = $$(element).removeClass('valid invalid');
            obj.len = obj.element$.length;
            if (obj.len) {
                obj.element = obj.element$[0];
                obj.value = obj.element.value || '';
            }
        }
        return obj;
    }

    function getValidDate(date){

        if (!date.match('today') && !date.match(regex[date])) {
            return false;
        }

        var validDate = new Date(),
            validDateArray;

        if (!date.match('today')) {
            validDateArray = date.split(/[\s.-]+/);
            validDate.setFullYear(validDateArray[0]);
            validDate.setMonth(validDateArray[1] - 1);
            validDate.setDate(validDateArray[2]);
        }

        return validDate;

    }


    // CONSTRUCTOR

    function Validator(element){
        extend(this, init(element));
    }

    Validator.fn = Validator.prototype;

    Validator.fn.init = function(element){
        if (element){
            extend(this, init(element));
        }
        return this;
    };

    // reset element so it can be validated again
    Validator.fn.reset = function(element){
        extend(this, init(element||this.element));
        return this;
    };

    // explicitly set a value to check
    // XNAT.validate().val('bar@foo.org').is('email').check();
    // -> true
    Validator.fn.val = function(value){
        this.value = value;
        return this;
    };

    Validator.fn.trim = function(){
        this.trimValue = true;
        this.element$.each(function(){
            this.value = this.value.trim();
        });
        return this;
    };

    // set className to valid/invalid
    Validator.fn.setClass = function(){
        var className = this.validated ? 'valid': 'invalid';
        this.element$
            .removeClass('valid invalid')
            .addClass(className);
    };

    Validator.fn.is = function(type, args){

        // check all if there's more than
        // one element in the selection
        if (this.len > 1) {
            this.all(type, args);
            return this;
        }

        // return early if the validation is already false
        // (this is necessary for working with chained methods)
        if (this.validated === false) { return this }

        if (this.trimValue) {
            this.value = (this.value+'').trim();
        }

        // if there's a test['test'] method, use that
        if (typeof test[type] == 'function') {
            this.validated = test[type](this.value, args);
        }
        // if there's a regex defined (above) for 'type', use that
        else if (regex[type]) {
            this.pattern(regex[type]);
            // this.validated = regex[type].test(this.value);
        }
        // if 'type' is a string, do a comparison
        else if (typeof type == 'string') {
            this.validated = test.equals(this.value, type);
        }
        // a 'type' function can also be passed
        // (must return boolean true or false)
        else if (typeof type == 'function') {
            this.validated = type.apply(this, [].concat(args));
        }
        // otherwise do a regex test
        else {
            try {
                this.pattern(type);
                // this.validated = type.test(this.value);
            }
            catch(e) {
                console.log(e);
            }
        }

        this.setClass();

        return this;
    };

    Validator.fn.not = function(type){
        this.is(type);
        this.validated = !this.validated;
        return this;
    };

    // validate all elements in a collection
    // XNAT.validate('input.float').all('float');
    Validator.fn.all = function(type, args){
        // var self = this;
        var invalid = 0;
        this.elements = this.element$.toArray();
        if (!type) return this;
        // if type is specified, check each element
        this.element$.each(function(){
            var elValidate = new Validator(this);
            elValidate.is(type, args);
            //valid = regex[type].test(this.value);
            if (elValidate.isValid(false)) {
                invalid++
            }
        });
        this.validated = invalid === 0;
        return this;
    };

    Validator.fn.none = function(type){
        // make sure NONE of the elements match the type
    };

    Validator.fn.pattern = function(regex){
        this.regex = regex;
        this.validated = regex.test(this.value);
        return this;
    };

    // match the value of another element
    // optionally trimming leading and trailing whitespace
    Validator.fn.matches = function(target, trim){
        var sourceValue = this.value + '';
        var targetValue = $$(target).val() + '';
        if (trim) {
            sourceValue = sourceValue.trim();
            targetValue = targetValue.trim();
        }
        this.validated = sourceValue === targetValue;
        return this;
    };

    // XNAT.validate('#url').required().check();
    Validator.fn.required = function(){
        this.all(function(){
            if (/checkbox|radio/.test(this.element$.type)) {
                return (this.checked === true);
            }
            return this.value+'' !== '';
        });
        return this;
    };

    // set up shortcut methods (uses test[type]() methods)
    // example:
    // XNAT.validate('#sessions-concurrent-max').lessThan(1000).check();
    [   'minLength', 'maxLength', 'exactLength', 'isLength',
        'greaterThan', 'greaterThanOrEqual', 'greaterThanOrEqualTo', 'gte',
        'lessThan', 'lessThanOrEqual', 'lessThanOrEqualTo', 'lte',
        'equalTo', 'equals', 'fileType'   ].forEach(function(method) {

        Validator.fn[method] = function (test) {
            this.is(method, test);
            return this;
        }

    });

    // .valid() must be called LAST
    // XNAT.validate('#email').trim().is('email').valid(true);
    Validator.fn.valid = function(bool){
        bool = (bool === undefined) ? true : bool;
        return bool ? this.validated : !this.validated;
    };
    Validator.fn.isValid = Validator.fn.valid;
    //
    // call *either* .valid() -OR- .check() last
    //
    // XNAT.validate('input.email').is('email').valid(true);
    //
    // --OR--
    //
    // XNAT.validate().value('foo').check(function(){ return this.value === 'foo' });
    //
    // .check() must be called last
    // XNAT.validate('#email').check('email');
    //
    // type can be regex['type'] string,
    // function (must return true or false),
    // or custom regex
    Validator.fn.check = function(type){
        if (type) {
            this.is(type);
        }
        return this.isValid(true);
    };

    // usage:
    // XNAT.validate('#user-email').trim().is('email').check();

    validate = function(element){
        return new Validator(element);
    };


    // TODO: move the date methods below to {test} object: test['greaterThanDate']() etc...
    validate.check = {

        greater_than_date: function(field, date){
            var enteredDate = getValidDate(field.value),
                validDate   = getValidDate(date);
            if (!validDate || !enteredDate) {
                return false;
            }
            return enteredDate > validDate;
        },

        less_than_date: function(field, date){
            var enteredDate = getValidDate(field.value),
                validDate   = getValidDate(date);
            if (!validDate || !enteredDate) {
                return false;
            }
            return enteredDate < validDate;
        },

        greater_than_or_equal_date: function(field, date){
            var enteredDate = getValidDate(field.value),
                validDate   = getValidDate(date);
            if (!validDate || !enteredDate) {
                return false;
            }
            return enteredDate >= validDate;
        },

        less_than_or_equal_date: function(field, date){
            var enteredDate = getValidDate(field.value),
                validDate   = getValidDate(date);
            if (!validDate || !enteredDate) {
                return false;
            }
            return enteredDate <= validDate;
        }
    };


    // this script has loaded
    validate.loaded = true;

    return XNAT.validate = validate;

}));
