package io.mosip.registration.util.healthcheck;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.micrometer.core.annotation.Timed;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.util.restclient.RestClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registration Health Checker Utility.
 *
 * @author Sivasankar Thalavai
 * @since 1.0.0
 */

@Component
public class RegistrationAppHealthCheckUtil {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationAppHealthCheckUtil.class);

	/**
	 * This method checks for the Disk Space Availability.
	 * 
	 * <p>
	 * Gets the {@link FileSystem} of the {@link OperatingSystem} that is currently
	 * used. Gets all the {@link OSFileStore} from the fileSystem and takes the
	 * FileStore that matches the current directory and then checks whether the
	 * usable space of the fileStore is greater than required disk space threshold.
	 * If it is greater, then it indicates that the required disk space is
	 * available, else, the required space is not available.
	 * </p>
	 *
	 * @return true, if is disk space available and false, if space is not
	 *         available.
	 */
	public static boolean isDiskSpaceAvailable() {
		LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
				APPLICATION_ID, "Registration Disk Space Checker had been called.");
		boolean isSpaceAvailable = false;
		long diskSpaceThreshold = 80000L;
		try {
			// java.nio resolves the file store of the working directory on every OS
			// (the previous OSHI based lookup only matched Windows drive letters like "C:\")
			FileStore fileStore = Files.getFileStore(Path.of(System.getProperty("user.dir")));
			isSpaceAvailable = fileStore.getUsableSpace() > diskSpaceThreshold;
			LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
					APPLICATION_ID, isSpaceAvailable ? "Required Disk Space Available." : "Required Disk Space Not Available.");
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
					APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		LOGGER.info("REGISTRATION - REGISTRATIONAPPHEALTHCHECKUTIL - ISDISKSPACEAVAILABLE", APPLICATION_NAME,
				APPLICATION_ID, "Registration Disk Space Checker had been ended.");
		return isSpaceAvailable;
	}
}
