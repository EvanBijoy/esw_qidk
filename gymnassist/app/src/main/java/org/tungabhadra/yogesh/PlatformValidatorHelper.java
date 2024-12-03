package org.tungabhadra.yogesh;

import android.app.Application;
import com.qualcomm.qti.platformvalidator.PlatformValidator;
import com.qualcomm.qti.platformvalidator.PlatformValidatorUtil;

public class PlatformValidatorHelper {

    private final PlatformValidator platformValidator;
    private final Application application;

    public PlatformValidatorHelper(Application application) {
        this.application = application;
        this.platformValidator = new PlatformValidator(PlatformValidatorUtil.Runtime.DSP);
    }

    public void printPlatformInfo() {
        // Check if DSP runtime is available
        boolean isRuntimeAvailable = platformValidator.isRuntimeAvailable(application);
        System.out.println("Runtime available: " + isRuntimeAvailable);

        // Check if Qualcomm Neural Processing SDK runtime is available
        boolean runtimeCheck = platformValidator.runtimeCheck(application);
        System.out.println("Runtime check passed: " + runtimeCheck);

        // Get core version information
        String coreVersion = platformValidator.coreVersion(application);
        System.out.println("Core version: " + coreVersion);
    }
}
