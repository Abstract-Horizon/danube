/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Creative Sphere - initial API and implementation
 *
 */
package org.abstracthorizon.danube.webdav.util;

import org.abstracthorizon.danube.webdav.xml.WebDAVXMLHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Test case for XML handler
 *
 * @author Daniel Sendula
 */
public class WebDAVXMLHandlerTest extends TestCase {

    public static String CRLF = "\n";

    protected String performTest(String input) throws Exception {
        SimpleNamespacesProvider namespacesProvider = new SimpleNamespacesProvider();
        DAVNamespace davNamespace = new DAVNamespace();
        namespacesProvider.addNamespace(davNamespace.getURLString(), davNamespace.getPreferredPrefix(), davNamespace);

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        InputSource inputSource = new InputSource(new ByteArrayInputStream(input.getBytes()));
        WebDAVXMLHandler handler = new WebDAVXMLHandler(namespacesProvider);
        parser.parse(inputSource, handler);

        Object result = handler.getResultObject();
        return result.toString();
    }

    public void testLockInfo() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
        + "      <D:lockinfo xmlns:D=\"DAV:\">" + CRLF
        + "        <D:locktype><D:write/></D:locktype>" + CRLF
        + "        <D:lockscope><D:exclusive/></D:lockscope>" + CRLF
        + "        <D:owner>" + CRLF
        + "             <D:href>http://www.ics.uci.edu/~ejw/contact.html</D:href>" + CRLF
        + "        </D:owner>" + CRLF
        + "      </D:lockinfo>" + CRLF;

        String result = performTest(input);
        Assert.assertEquals("LockInfo[LockScope[exclusive=true],LockType[write],Owner[HRef[http://www.ics.uci.edu/~ejw/contact.html]]]", result);

        input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
        + "      <lockinfo xmlns=\"DAV:\">" + CRLF
        + "        <locktype><write/></locktype>" + CRLF
        + "        <lockscope><exclusive/></lockscope>" + CRLF
        + "        <owner>" + CRLF
        + "             <href>http://www.ics.uci.edu/~ejw/contact.html</href>" + CRLF
        + "        </owner>" + CRLF
        + "      </lockinfo>" + CRLF;

        result = performTest(input);
        Assert.assertEquals("LockInfo[LockScope[exclusive=true],LockType[write],Owner[HRef[http://www.ics.uci.edu/~ejw/contact.html]]]", result);

    }

    public void testPropertyUpdate() throws Exception {
        String input = "<?xml version=\"1.0\" ?>" + CRLF
                       + "<D:propertyupdate xmlns:D=\"DAV:\"" + CRLF
                       + "    xmlns:Z=\"http://www.w3.com/standards/z39.50/\">" + CRLF
                       + "    <D:set>" + CRLF
                       + "        <D:prop>" + CRLF
                       + "            <D:creationdate>31082008</D:creationdate>" + CRLF
                       + "        </D:prop>" + CRLF
                       + "    </D:set>" + CRLF
                       + "    <D:set>" + CRLF
                       + "        <D:prop>" + CRLF
                       + "            <Z:authors>" + CRLF
                       + "                <Z:Author>Jim Whitehead</Z:Author>" + CRLF
                       + "                <Z:Author>Roy Fielding</Z:Author>" + CRLF
                       + "            </Z:authors>" + CRLF
                       + "         </D:prop>" + CRLF
                       + "    </D:set>" + CRLF
                       + "    <D:remove>" + CRLF
                       + "        <D:prop><Z:Copyright-Owner/></D:prop>" + CRLF
                       + "    </D:remove>" + CRLF
                       + "</D:propertyupdate>" + CRLF;


        String result = performTest(input);
        Assert.assertEquals("PropertyUpdate[Set[Prop[CreationDate[31082008]]],Set[Prop[]],Remove[Prop[]]]", result);
    }

    public void testPropFind() throws Exception {
//        String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
//        + "      <D:propfind xmlns:D=\"DAV:\">" + CRLF
//        + "        <D:prop xmlns:R=\"http://www.foo.bar/boxschema/\">" + CRLF
//        + "             <R:bigbox/>" + CRLF
//        + "             <R:author/>" + CRLF
//        + "             <R:DingALing/>" + CRLF
//        + "             <R:Random/>" + CRLF
//        + "        </D:prop>" + CRLF
//        + "      </D:propfind>" + CRLF;
//        String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><D:propfind xmlns:D=\"DAV:\"><D:prop><D:creationdate/><D:getcontentlength/><D:displayname/><D:source/><D:getcontentlanguage/><D:getcontenttype/><D:executable/><D:getlastmodified/><D:getetag/><D:supportedlock/><D:lockdiscovery/><D:resourcetype/></D:prop></D:propfind>";
        String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
        + "      <D:propfind xmlns:D=\"DAV:\">" + CRLF
        + "        <D:prop>" + CRLF
        + "             <D:creationdate/>" + CRLF
        + "             <D:getlastmodified/>" + CRLF
        + "        </D:prop>" + CRLF
        + "      </D:propfind>" + CRLF;

        String result = performTest(input);
        Assert.assertEquals("PropFind[Prop[CreationDate[],GetLastModified[]]]", result);


    }

    public void testPropertyBehavior() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
        + "        <d:propertybehavior xmlns:d='DAV:'>" + CRLF
        + "        <d:keepalive>*</d:keepalive>" + CRLF
        + "      </d:propertybehavior>" + CRLF;

        String result = performTest(input);
        Assert.assertEquals("PropertyBehavior[KeepAlive[*]]", result);

        input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + CRLF
        + "        <d:propertybehavior xmlns:d='DAV:'>" + CRLF
        + "        <d:keepalive>" + CRLF
        + "            <d:href>http://something/</d:href>" + CRLF
        + "            <d:href>http://something/else</d:href>" + CRLF
        + "            <d:href>http://something:8080/index.html</d:href>" + CRLF
        + "        </d:keepalive>" + CRLF
        + "      </d:propertybehavior>" + CRLF;

        result = performTest(input);
        Assert.assertEquals("PropertyBehavior[KeepAlive[HRef[http://something/],HRef[http://something/else],HRef[http://something:8080/index.html]]]", result);

    }
}
