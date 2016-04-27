$(document).ready(function() {
    $('#xnat_name').text(XNAT.app.siteId);

    var siteAdminNamespace = "siteAdmin";

    $.getJSON('/xnat17/xapi/spawner/ids/'+siteAdminNamespace).done(function(data){
        console.log(data);
        $(data).each(function(i,id){
            console.log(id);
            renderTab(id);
        });
    });

    function renderTab(id){
        $.getJSON('/xnat17/xapi/spawner/resolve/'+siteAdminNamespace+'/'+id).done(function(data){

            console.log(id);

            console.log(data);
            //var adminTabs = XNAT.ui.tabs.init(data.Result).render('#admin-config-tabs');
            //console.log(adminTabs);
        });
    }
});

