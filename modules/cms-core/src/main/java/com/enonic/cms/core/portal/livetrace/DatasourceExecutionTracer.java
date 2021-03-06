/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import java.util.Map;

import com.enonic.cms.core.portal.datasource.DataSourceType;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public class DatasourceExecutionTracer
{
    public static DatasourceExecutionTrace startTracing( DataSourceType datasourcesType, String datasourceMethodName,
                                                         LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        if ( datasourceMethodName == null )
        {
            return null;
        }

        return livePortalTraceService.startDatasourceExecutionTracing( datasourcesType, datasourceMethodName );
    }

    public static void stopTracing( DatasourceExecutionTrace trace, LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null && livePortalTraceService != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceRunnableCondition( DatasourceExecutionTrace trace, String runnableCondition )
    {
        if ( trace != null )
        {
            trace.setRunnableCondition( runnableCondition );
        }
    }

    public static void traceIsExecuted( DatasourceExecutionTrace trace, boolean isExecuted )
    {
        if ( trace != null )
        {
            trace.setExecuted( isExecuted );
        }
    }

    public static void traceMethodCall( DataSourceRequest request, DatasourceExecutionTrace trace )
    {
        if ( trace != null && request != null )
        {
            for ( final Map.Entry<String, String> param : request.getParams().entrySet() )
            {
                trace.addDatasourceMethodArgument( new DatasourceMethodArgument( param.getKey(), param.getValue() ) );
            }
        }
    }

    public static void traceIsCacheUsed( boolean cacheUsed, LivePortalTraceService livePortalTraceService )
    {
        if ( livePortalTraceService == null )
        {
            return;
        }

        DatasourceExecutionTrace trace = livePortalTraceService.getCurrentTrace().getDatasourceExecutionTrace();
        if ( trace != null )
        {
            trace.setCacheUsed( cacheUsed );
        }
    }
}
