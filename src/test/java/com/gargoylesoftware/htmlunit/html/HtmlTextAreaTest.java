/*
 * Copyright (c) 2002, 2005 Gargoyle Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment:
 *
 *       "This product includes software developed by Gargoyle Software Inc.
 *        (http://www.GargoyleSoftware.com/)."
 *
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 4. The name "Gargoyle Software" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact info@GargoyleSoftware.com.
 * 5. Products derived from this software may not be called "HtmlUnit", nor may
 *    "HtmlUnit" appear in their name, without prior written permission of
 *    Gargoyle Software Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GARGOYLE
 * SOFTWARE INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gargoylesoftware.htmlunit.html;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 *  Tests for HtmlTextArea
 *
 * @version  $Revision$
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Marc Guillemot
 */
public class HtmlTextAreaTest extends WebTestCase {
    /**
     *  Create an instance
     *
     * @param  name The name of the test
     */
    public HtmlTextAreaTest( final String name ) {
        super( name );
    }


    /**
     * @throws Exception if the test fails
     */
    public void testFormSubmission_OriginalData()
        throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title></head><body>"
            + "<form id='form1'>"
            + "<textarea name='textArea1'>foo</textarea>"
            + "</form></body></html>";
        final HtmlPage page = loadPage(htmlContent);
        final MockWebConnection webConnection = getMockConnection(page);
        final HtmlForm form = ( HtmlForm )page.getHtmlElementById( "form1" );

        final HtmlTextArea textArea
            = ( HtmlTextArea )form.getTextAreasByName( "textArea1" ).get( 0 );
        assertNotNull(textArea);

        final Page secondPage = form.submit();

        assertEquals("url", URL_GARGOYLE.toExternalForm() + "?textArea1=foo", 
                secondPage.getWebResponse().getUrl().toExternalForm());
        assertEquals( "method", SubmitMethod.GET, webConnection.getLastMethod() );
    }


    /**
     * @throws Exception if the test fails
     */
    public void testFormSubmission_NewValue()
        throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title></head><body>"
            + "<form id='form1'>"
            + "<textarea name='textArea1'>foo</textarea>"
            + "</form></body></html>";
        final HtmlPage page = loadPage(htmlContent);
        final MockWebConnection webConnection = getMockConnection(page);
        final HtmlForm form = ( HtmlForm )page.getHtmlElementById( "form1" );

        final HtmlTextArea textArea
            = ( HtmlTextArea )form.getTextAreasByName( "textArea1" ).get( 0 );
        textArea.setText( "Flintstone" );
        final Page secondPage = form.submit();

        assertEquals("url", URL_GARGOYLE.toExternalForm() + "?textArea1=Flintstone", 
                secondPage.getWebResponse().getUrl().toExternalForm());
        assertEquals( "method", SubmitMethod.GET, webConnection.getLastMethod() );
    }
    /**
     * @throws Exception if the test fails
     */
    public void testGetText() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title></head><body>"
            + "<form id='form1'>"
            + "<textarea name='textArea1'> foo \n bar </textarea>"
            + "</form></body></html>";
        final HtmlPage page = loadPage(htmlContent);
        final HtmlForm form = ( HtmlForm )page.getHtmlElementById( "form1" );

        final HtmlTextArea textArea
            = ( HtmlTextArea )form.getTextAreasByName( "textArea1" ).get( 0 );
        assertNotNull(textArea);
        assertEquals("White space must be preserved!", " foo \n bar ", textArea.getText());
    }
}

