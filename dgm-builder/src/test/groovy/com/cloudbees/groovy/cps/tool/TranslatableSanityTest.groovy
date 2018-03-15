/*
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloudbees.groovy.cps.tool

import org.hamcrest.Matchers
import org.junit.Test

import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue


class TranslatableSanityTest {
    @Test
    void translatableTxtSanity() {
        InputStream is = Translator.class.getResourceAsStream("translatable.txt")

        List<String> origLines = []

        try {
            origLines = is.readLines()
        } finally {
            is.close()
        }

        List<String> duplicateLines = origLines.countBy { it }.findAll { it.value > 1 }.collect { it.key }

        assertTrue("Duplicate translatable signatures detected:\n${duplicateLines.collect { "- ${it}" }.join("\n") }",
            duplicateLines.isEmpty())

        // We're sorting ignoring generics because those just make it harder to figure out where to go.
        List<String> sortedLines = origLines.sort(false) { it.replaceAll(/<.*?>/, '') }

        assertThat("Translatable signatures not sorted", origLines, Matchers.contains(sortedLines.toArray()))
    }
}
