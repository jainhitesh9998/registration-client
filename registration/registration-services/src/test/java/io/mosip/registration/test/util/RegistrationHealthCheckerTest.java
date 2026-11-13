package io.mosip.registration.test.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.InjectMocks;

import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

public class RegistrationHealthCheckerTest {

	@InjectMocks
	private RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;

	@Test
	public void isDiskSpaceAvailableTestTrue() {
		assertNotNull(RegistrationAppHealthCheckUtil.isDiskSpaceAvailable());

	}


}
