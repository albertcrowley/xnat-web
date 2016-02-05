/*
 * org.nrg.xnat.security.alias.ClearExpiredAliasTokensJob
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/10/13 9:04 PM
 */
package org.nrg.xnat.security.alias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class ClearExpiredAliasTokens implements Callable<Void> {
    public ClearExpiredAliasTokens(final DataSource dataSource, final String timeout) {
        if (_log.isDebugEnabled()) {
            _log.debug("Initializing the alias token sweeper job with an interval of: " + timeout);
        }

        _dataSource = dataSource;
        _timeout = timeout;
    }

    /**
     */
    @Override
    public Void call() {
        if (_log.isDebugEnabled()) {
            _log.debug("Executing alias token sweep function");
        }
        JdbcTemplate template = new JdbcTemplate(_dataSource);
        for (final String format : ALIAS_TOKEN_QUERIES) {
            final String query = String.format(format, _timeout);
            if (_log.isDebugEnabled()) {
                _log.debug("Executing alias token sweep query: " + query);
            }
            template.execute(query);
        }
        return null;
    }

    private static final Logger _log = LoggerFactory.getLogger(ClearExpiredAliasTokens.class);
    private static final String QUERY_DELETE_TOKEN_IP_ADDRESSES = "DELETE FROM xhbm_alias_token_validipaddresses WHERE alias_token in (SELECT id FROM xhbm_alias_token WHERE created < NOW() - INTERVAL '%s')";
    private static final String QUERY_DELETE_ALIAS_TOKENS = "DELETE FROM xhbm_alias_token WHERE created < NOW() - INTERVAL '%s'";
    private static final List<String> ALIAS_TOKEN_QUERIES = Arrays.asList(QUERY_DELETE_TOKEN_IP_ADDRESSES, QUERY_DELETE_ALIAS_TOKENS);

    private final DataSource _dataSource;
    private final String _timeout;
}