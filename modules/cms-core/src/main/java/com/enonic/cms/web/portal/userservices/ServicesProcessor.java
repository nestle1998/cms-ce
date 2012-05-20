package com.enonic.cms.web.portal.userservices;

import com.enonic.cms.web.portal.PortalWebContext;

public interface ServicesProcessor
{
    public String getHandlerName();

    public void handle( final PortalWebContext context )
        throws Exception;
}
