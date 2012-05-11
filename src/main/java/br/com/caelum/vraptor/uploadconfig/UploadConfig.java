package br.com.caelum.vraptor.uploadconfig;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Component
public class UploadConfig extends DefaultMultipartConfig {
	private final UrlToResourceTranslator translator;
	private final RequestInfo info;

	UploadConfig(UrlToResourceTranslator translator, RequestInfo info) {
		this.translator = translator;
		this.info = info;
	}

	public long getSizeLimit() {
		ResourceMethod method = translator.translate(info);

		if (method.containsAnnotation(SizeLimit.class)) {
			return method.getMethod().getAnnotation(SizeLimit.class).value();
		} else {
			return 300 * 1024L;
		}
	}
}
