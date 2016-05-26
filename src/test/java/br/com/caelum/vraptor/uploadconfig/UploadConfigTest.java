package br.com.caelum.vraptor.uploadconfig;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class UploadConfigTest {

	private static final long TEST_LIMIT = 987654321L;
	@Mock
	private UrlToResourceTranslator translator;
	@Mock
	private RequestInfo info;

	private UploadConfig uploadConfig;

	private final class MyResourceMethod implements ResourceMethod {

		private final Method method;

		public MyResourceMethod(Method method) {
			this.method = method;
		}

		@Override
		public Method getMethod() {
			return method;
		}

		@Override
		public ResourceClass getResource() {
			return null;
		}

		@Override
		public boolean containsAnnotation(Class<? extends Annotation> annotation) {
			return method.isAnnotationPresent(annotation);
		}

	}

	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		initMocks(this);
		when(translator.translate(info)).thenReturn(
				new MyResourceMethod(getClass().getMethod("unlimitedMethod")));
		uploadConfig = new UploadConfig(translator, info);
	}

	public void unlimitedMethod() {
		// dummy unlimited method
	}

	@SizeLimit(value = TEST_LIMIT)
	public void limitedMethod() {
		// dummy limited method
	}

	@Test
	public void defaultSizeLimitShouldBeDifferentFromTestConstant() {
		assertThat(uploadConfig.getSizeLimit(), is(not(equalTo(TEST_LIMIT))));
	}

	@Test
	public void getSizeLimitShoultReturnDefaultSize() {
		assertThat(uploadConfig.getSizeLimit(), is(equalTo(300 * 1024L)));
	}

	@Test
	public void getSizeLimitShoultReturnCustomSize() throws SecurityException,
			NoSuchMethodException {
		when(translator.translate(info)).thenReturn(
				new MyResourceMethod(getClass().getMethod("limitedMethod")));
		assertThat(uploadConfig.getSizeLimit(), is(equalTo(TEST_LIMIT)));
	}

}
