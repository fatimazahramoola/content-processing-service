package com.fatimazahramoola.contentprocessing.processing;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltExecutable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class XsltTransformer {

	private static final String STYLESHEET_PATH = "xslt/normalize-document.xsl";

	private final Processor processor;
	private final XsltExecutable stylesheet;

	public XsltTransformer() {
		try {
			this.processor = new Processor(false);
			this.stylesheet = processor.newXsltCompiler()
					.compile(new StreamSource(new ClassPathResource(STYLESHEET_PATH).getInputStream()));
		}
		catch (Exception exception) {
			throw new IllegalStateException("Could not load XML normalization stylesheet.", exception);
		}
	}

	public String transform(String xml) {
		try {
			StringWriter output = new StringWriter();
			Serializer serializer = processor.newSerializer(output);
			Xslt30Transformer transformer = stylesheet.load30();
			transformer.transform(new StreamSource(new StringReader(xml)), serializer);
			return output.toString();
		}
		catch (SaxonApiException exception) {
			throw new IllegalStateException("Could not transform XML document.", exception);
		}
	}

}
