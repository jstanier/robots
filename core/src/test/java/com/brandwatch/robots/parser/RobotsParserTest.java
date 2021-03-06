package com.brandwatch.robots.parser;

/*
 * #%L
 * Robots (core)
 * %%
 * Copyright (C) 2015 Brandwatch
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Brandwatch nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.withSettings;

@RunWith(MockitoJUnitRunner.class)
public class RobotsParserTest {

    @Mock
    private RobotsParseHandler handler;

    @Test(expected = NullPointerException.class)
    public void givenNullInputStream_whenNewInstance_thenThrowsNPE() {
        new RobotsParser((InputStream) null);
    }


    @Test
    public void givenEmptyStream_whenParser_thenNoExceptionThrown() throws IOException, ParseException {
        final InputStream inputStream = ByteSource.empty().openStream();
        RobotsParser robotsTxtParser = new RobotsParser(inputStream);
        robotsTxtParser.parse(handler);
    }

    @Test
    public void givenEmptyReader_whenParse_thenNoExceptionThrown() throws IOException, ParseException {
        Reader reader = CharSource.empty().openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }

    @Test
    public void givenSingleBlankLine_whenParse_thenNoExceptionThrown() throws IOException, ParseException {
        Reader reader = CharSource.wrap("\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }

    @Test
    public void givenSingleCommentLine_whenParse_thenNoExceptionThrown() throws IOException, ParseException {
        Reader reader = CharSource.wrap("# comment line \n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }

    @Test
    public void givenTwoCommentLines_whenParse_thenNoExceptionThrown() throws IOException, ParseException {
        Reader reader = CharSource.wrap("# comment line 1 \n# comment line 2 \n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }


    @Test
    public void givenLowerCaseUserAgentLine_whenParse_thenHandlerCalledWithExpectedAgent() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }

    @Test
    public void givenUpperCaseUserAgentLine_whenParse_thenHandlerCalledWithExpectedAgent() throws IOException, ParseException {
        Reader reader = CharSource.wrap("USER-AGENT: example-bot\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }

    @Test
    public void givenMixedCaseUserAgentLine_whenParse_thenHandlerCalledWithExpectedAgent() throws IOException, ParseException {
        Reader reader = CharSource.wrap("uSeR-aGeNt: example-bot\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }

    @Test
    public void givenTwoUserAgentLines_whenParse_thenHandlerCalledWithExpectedAgent() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: first\nuser-agent: second\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("first");
        verify(handler).userAgent("second");
    }

    @Test
    public void givenMixedCaseUserAgentLine_whenParse_thenStartEntryCalled() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).startEntry();
    }

    @Test
    public void givenMixedCaseUserAgentLine_whenParse_thenEndEntryCalled() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).endEntry();
    }

    @Test
    public void givenEmpty_whenParse_thenZeroInteractionOnHandler() throws IOException, ParseException {
        Reader reader = CharSource.empty().openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verifyZeroInteractions(handler);
    }

    @Test
    public void givenUserAgentMissingEOL_whenParse_thenEndEntryCalled() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).endEntry();
    }

    @Test
    public void givenEmptyDisallow_whenParse_thenDisallowCalledWithEmptyString() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\ndisallow:\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).disallow("");
    }


    @Test
    public void givenUserAgentWithTrailingSpace_whenParse_thenAgentIsTrimmed() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot \n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }


    @Test
    public void givenUserAgentEndingInComment_whenParse_thenAgentProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot # some comment\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }


    @Test
    public void givenBlankLineSeparatedAgents_whenParse_thenAgentsProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\n\n\n\nuser-agent: naughty-bot").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
        verify(handler).userAgent("naughty-bot");
    }


    @Test
    public void givenTrailingNewLines_whenParse_thenAgentProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: example-bot\n\n\n\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).userAgent("example-bot");
    }

    @Test
    public void givenHostRule_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\nhost: example.com\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).otherDirective("host", "example.com");
    }


    @Test
    public void givenCrawlDelayRule_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\ncrawl-delay: 10\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).otherDirective("crawl-delay", "10");
    }


    @Test
    public void givenUnsupportedRule_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\nCheese-burgers: yummy\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).otherDirective("Cheese-burgers", "yummy");
    }

    @Test
    public void givenMissingFinalEOL_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\nallow: /").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).allow("/");
    }

    @Test
    public void givenMissingPath_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\nallow:\n").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).allow("");
    }

    @Test
    public void givenMissingPathAndEOL_whenParse_thenDirectiveProduced() throws IOException, ParseException {
        Reader reader = CharSource.wrap("user-agent: *\nallow:").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
        verify(handler).allow("");
    }

    @Test
    public void givenCommentMissingEOL_whenParse_thenNoExceptionThrown() throws IOException, ParseException {
        Reader reader = CharSource.wrap("#").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }

    @Test(expected = ParseException.class)
    public void givenInputIsHtml_whenParse_thenThrowsParseException() throws IOException, ParseException {
        Reader reader = CharSource.wrap(
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n</head>\n" +
                "<body>\n" +
                "<h1>Some text</h1>\n" +
                "</body>\n" +
                "</html>").openStream();
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        robotsTxtParser.parse(handler);
    }

    @Test
    public void givenUtf8EncodingInputStream_whenParser_thenNoExceptionThrown() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.UTF_8);
    }

    @Test(expected = ParseException.class)
    public void givenUtf16EncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.UTF_16);
    }

    @Test(expected = ParseException.class)
    public void givenUtf16BEEncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.UTF_16BE);
    }

    @Test(expected = ParseException.class)
    public void givenUtf16LEEncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.UTF_16LE);
    }

    @Test
    public void givenLatin1LEEncodingInputStream_whenParser_thenNoExceptionThrown() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.ISO_8859_1);
    }

    @Test
    public void givenAsciiLEEncodingInputStream_whenParser_thenNoExceptionThrown() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charsets.US_ASCII);
    }

    @Test(expected = ParseException.class)
    public void givenUtf32EncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charset.forName("UTF-32"));
    }

    @Test(expected = ParseException.class)
    public void givenUtf32BEEncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charset.forName("UTF-32BE"));
    }

    @Test(expected = ParseException.class)
    public void givenUtf32LEEncodingInputStream_whenParser_thenThrowsParseException() throws IOException, ParseException {
        givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charset.forName("UTF-32LE"));
    }

    private void givenWrongEncodingInputStream_whenParser_thenThrowsParseException(Charset actualEncoding) throws IOException, ParseException {
        handler = mock(RobotsParseHandler.class, withSettings().verboseLogging());
        String input = "user-agent: example-bot\nallow: /\n";
        byte[] inputBytes = input.getBytes(actualEncoding);
        System.out.println(Arrays.toString(inputBytes));

        final InputStream inputStream = ByteSource.wrap(inputBytes).openStream();
        RobotsParser robotsTxtParser = new RobotsParser(inputStream);

        robotsTxtParser.parse(handler);
    }

}
