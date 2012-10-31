/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.viewtransformer;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.transform.JDOMSource;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.PortalRenderingException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.structure.TemplateParameterType;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessor;

@Component
public class PageTemplateXsltViewTransformer
    extends AbstractXsltViewTransformer
{
    public ViewTransformationResult transform( ResourceFile xsltTemplateFile, Document xml, TransformationParams transformationParams )
    {
        try
        {
            XMLDocument xslt = xsltTemplateFile.getDataAsXml();
            PortalXsltProcessor processor = createProcessor( xsltTemplateFile.getResourceKey() );

            // Iterate over the parameters defined in the xslt template
            for ( Element parameterEl : findXsltParamElements( xslt.getAsJDOMDocument() ) )
            {
                TemplateParameterType parameterType = resolveTemplateParameterType( parameterEl );
                String parameterName = parameterEl.getAttributeValue( "name" );
                TransformationParameter parameter = transformationParams.get( parameterName );

                if ( parameter == null || parameter.getValue() == null )
                {
                    if ( TemplateParameterType.OBJECT.equals( parameterType ) || TemplateParameterType.PAGE.equals( parameterType ) ||
                        TemplateParameterType.CATEGORY.equals( parameterType ) || TemplateParameterType.CONTENT.equals( parameterType ) )
                    {
                        processor.setParameter( parameterName, "" );
                    }
                    continue;
                }

                processor.setParameter( parameter.getName(), parameter.getValue() );
            }

            String content = processor.process( new JDOMSource( xml ) );

            ViewTransformationResult result = new ViewTransformationResult();
            result.setHttpContentType( processor.getContentType() );
            result.setContent( content );
            result.setOutputMediaType( processor.getOutputMediaType() );
            result.setOutputEncoding( processor.getOutputEncoding() );
            result.setOutputMethod( processor.getOutputMethod() );
            return result;
        }
        catch ( XsltProcessorException e )
        {
            throw new PortalRenderingException( "Failed to transform page template view", e );
        }
    }

    private TemplateParameterType resolveTemplateParameterType( Element paramEl )
    {
        Element typeEl = JDOMUtil.getFirstElement( paramEl );
        if ( typeEl == null )
        {
            return null;
        }
        return TemplateParameterType.parse( typeEl.getText() );
    }

    @SuppressWarnings("unchecked")
    private Element[] findXsltParamElements( Document doc )
    {
        List list = doc.getRootElement().getChildren( "param", Namespace.getNamespace( XSLT_NS ) );
        return (Element[]) list.toArray( new Element[list.size()] );
    }
}
