/*
 * Copyright (c) 2002-2021 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host.css;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSOMParser;
import com.gargoylesoftware.css.parser.javacc.CSS3Parser;
import com.gargoylesoftware.css.parser.selector.Selector;
import com.gargoylesoftware.css.parser.selector.SelectorList;
import com.gargoylesoftware.css.parser.selector.SelectorListImpl;
import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCssErrorHandler;
import com.gargoylesoftware.htmlunit.SimpleWebTestCase;
import com.gargoylesoftware.htmlunit.css.CssStyleSheet;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLStyleElement;

/**
 * Unit tests for {@link CssStyleSheet}.
 *
 * @author Marc Guillemot
 * @author Ahmed Ashour
 * @author Frank Danek
 */
@RunWith(BrowserRunner.class)
public class CSSStyleSheet2Test extends SimpleWebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selects_miscSelectors() throws Exception {
        final String html = "<html><head><title>test</title>\n"
            + "</head><body><style></style>\n"
            + "<form name='f1' action='foo' class='yui-log'>\n"
            + "<div><div><input name='i1' id='m1'></div></div>\n"
            + "<input name='i2' class='yui-log'>\n"
            + "<button name='b1' class='yui-log'>\n"
            + "<button name='b2'>\n"
            + "</form>\n"
            + "</body></html>";

        final HtmlPage page = loadPage(html);
        final HtmlElement body = page.getBody();
        final HtmlForm form = page.getFormByName("f1");
        final HtmlInput input1 = (HtmlInput) page.getElementsByName("i1").get(0);
        final HtmlInput input2 = (HtmlInput) page.getElementsByName("i2").get(0);
        final DomElement button1 = page.getElementsByName("b1").get(0);
        final DomElement button2 = page.getElementsByName("b2").get(0);
        final BrowserVersion browserVersion = getBrowserVersion();

        final HtmlStyle node = (HtmlStyle) page.getElementsByTagName("style").item(0);

        Selector selector = parseSelector("*.yui-log input");

        assertFalse(CssStyleSheet.selects(browserVersion, selector, body, null, false));
        assertFalse(CssStyleSheet.selects(browserVersion, selector, form, null, false));
        assertTrue(CssStyleSheet.selects(browserVersion, selector, input1, null, false));
        assertTrue(CssStyleSheet.selects(browserVersion, selector, input2, null, false));
        assertFalse(CssStyleSheet.selects(browserVersion, selector, button1, null, false));
        assertFalse(CssStyleSheet.selects(browserVersion, selector, button2, null, false));

        selector = parseSelector("#m1");
        assertTrue(CssStyleSheet.selects(browserVersion, selector, input1, null, false));
        assertFalse(CssStyleSheet.selects(browserVersion, selector, input2, null, false));
    }

    private static Selector parseSelector(final String rule) {
        return parseSelectors(rule).get(0);
    }

    /**
     * Parses the selectors at the specified input source. If anything at all goes wrong, this
     * method returns an empty selector list.
     *
     * @param source the source from which to retrieve the selectors to be parsed
     * @return the selectors parsed from the specified input source
     */
    private static SelectorList parseSelectors(final String source) {
        SelectorList selectors;
        try {
            final CSSErrorHandler errorHandler = new DefaultCssErrorHandler();
            final CSSOMParser parser = new CSSOMParser(new CSS3Parser());
            parser.setErrorHandler(errorHandler);
            selectors = parser.parseSelectors(source);
            // in case of error parseSelectors returns null
            if (null == selectors) {
                selectors = new SelectorListImpl();
            }
        }
        catch (final Throwable t) {
//            if (LOG.isErrorEnabled()) {
//                LOG.error("Error parsing CSS selectors from '" + source + "': " + t.getMessage(), t);
//            }
            selectors = new SelectorListImpl();
        }
        return selectors;
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_anyNodeSelector() throws Exception {
        testSelects("*", true, true, true);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_childSelector() throws Exception {
        testSelects("body > div", false, true, false);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_descendantSelector() throws Exception {
        testSelects("body span", false, false, true);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_elementSelector() throws Exception {
        testSelects("div", false, true, false);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_directAdjacentSelector() throws Exception {
        testSelects("span + span", false, false, true);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_conditionalSelector_idCondition() throws Exception {
        testSelects("span#s", false, false, true);
        testSelects("#s", false, false, true);
        testSelects("span[id=s]", false, false, true);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selectsIdConditionWithSpecialChars() throws Exception {
        final String html =
                "<html><body><style></style>\n"
              + "<div id='d:e'></div>\n"
              + "<div id='d-e'></div>\n"
              + "</body></html>";
        final HtmlPage page = loadPage(html);
        final HtmlStyle node = (HtmlStyle) page.getElementsByTagName("style").item(0);
        final HTMLStyleElement host = (HTMLStyleElement) node.getScriptableObject();
        final BrowserVersion browserVersion = getBrowserVersion();

        Selector selector = parseSelectors("#d\\:e").get(0);
        assertTrue(CssStyleSheet.selects(browserVersion, selector, page.getHtmlElementById("d:e"), null, false));

        selector = parseSelectors("#d-e").get(0);
        assertTrue(CssStyleSheet.selects(browserVersion, selector, page.getHtmlElementById("d-e"), null, false));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_conditionalSelector_classCondition() throws Exception {
        testSelects("div.bar", false, true, false);
        testSelects(".bar", false, true, false);
        testSelects("div[class~=bar]", false, true, false);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_pseudoClass_root() throws Exception {
        testSelects(":root", false, false, false);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_pseudoClass_lang() throws Exception {
        testSelects(":lang(en)", false, true, true);
        testSelects(":lang(de)", false, false, false);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void selects_pseudoClass_negation() throws Exception {
        testSelects(":not(div)", true, false, true);
    }

    private void testSelects(final String css, final boolean selectBody, final boolean selectDivD,
        final boolean selectSpanS) throws Exception {
        final String html =
              "<html>\n"
            + "  <body id='b'>\n"
            + "    <style></style>\n"
            + "    <div id='d' class='foo bar' lang='en-GB'>\n"
            + "      <span>x</span>\n"
            + "      <span id='s'>a</span>b\n"
            + "    </div>\n"
            + "  </body>\n"
            + "</html>";
        final HtmlPage page = loadPage(html);
        final BrowserVersion browserVersion = getBrowserVersion();
        final HtmlStyle node = (HtmlStyle) page.getElementsByTagName("style").item(0);
        final HTMLStyleElement host = (HTMLStyleElement) node.getScriptableObject();
        final Selector selector = parseSelectors(css).get(0);
        assertEquals(selectBody,
                CssStyleSheet.selects(browserVersion, selector, page.getHtmlElementById("b"), null, false));
        assertEquals(selectDivD,
                CssStyleSheet.selects(browserVersion, selector, page.getHtmlElementById("d"), null, false));
        assertEquals(selectSpanS,
                CssStyleSheet.selects(browserVersion, selector, page.getHtmlElementById("s"), null, false));
    }

    /**
     * Test for #1300.
     * @throws Exception if the test fails
     */
    @Test
    public void brokenExternalCSS() throws Exception {
        final String html = "<html><head>\n"
            + "<link rel='stylesheet' type='text/css' href='" + URL_SECOND + "'/></head></html>";

        getMockWebConnection().setResponse(URL_SECOND, "body { font-weight: 900\\9; }");
        final HtmlPage htmlPage = loadPage(html);

        final NodeList list = htmlPage.getElementsByTagName("body");
        final HtmlElement element = (HtmlElement) list.item(0);
        final ComputedCSSStyleDeclaration style = ((HTMLElement) element.getScriptableObject()).getCurrentStyle();
        assertEquals("CSSStyleDeclaration for ''", style.toString());
    }

}
