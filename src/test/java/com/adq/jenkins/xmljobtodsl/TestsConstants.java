package com.adq.jenkins.xmljobtodsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestsConstants {

    public static final JobDescriptor getJobDescriptor() {
        List<PropertyDescriptor> buildBlockProperties = new ArrayList<>();
        buildBlockProperties.add(new PropertyDescriptor("useBuildBlocker", "true"));
        buildBlockProperties.add(new PropertyDescriptor("blockingJobs", "Build-iOS-App"));
        buildBlockProperties.add(new PropertyDescriptor("blockLevel", "GLOBAL"));
        buildBlockProperties.add(new PropertyDescriptor("scanQueueFor", "DISABLED"));

        List<PropertyDescriptor> propertiesProperties = new ArrayList<>();
        propertiesProperties.add(new PropertyDescriptor("hudson.plugins.buildblocker.BuildBlockerProperty", buildBlockProperties));

        List<PropertyDescriptor> buildNameUpdaterProperties = new ArrayList<>();
        buildNameUpdaterProperties.add(new PropertyDescriptor("macroTemplate", "Test iOS App #${BUILD_NUMBER} | ${APP_VERSION}"));
        buildNameUpdaterProperties.add(new PropertyDescriptor("fromFile", "false"));
        buildNameUpdaterProperties.add(new PropertyDescriptor("fromMacro", "true"));
        buildNameUpdaterProperties.add(new PropertyDescriptor("macroFirst", "true"));

        List<PropertyDescriptor> shellProperties = new ArrayList<>();
        shellProperties.add(new PropertyDescriptor("command", "export PLATFORM=${platform}\n" +
                "               cd 'iOSTest-AppiumTests/src/scripts/'\n" +
                "               fab -f build.py initialize_env run_appium run_tests kill_appium"));

        List<PropertyDescriptor> buildersProperties = new ArrayList<>();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("plugin", "build-name-setter@1.6.5");
        buildersProperties.add(new PropertyDescriptor("org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater", null, buildNameUpdaterProperties, attributes));
        buildersProperties.add(new PropertyDescriptor("hudson.tasks.Shell", shellProperties));

        List<PropertyDescriptor> projectProperties = new ArrayList<>();
        projectProperties.add(new PropertyDescriptor("properties", propertiesProperties));
        projectProperties.add(new PropertyDescriptor("builders", buildersProperties));

        List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(new PropertyDescriptor("project", projectProperties));

        return new JobDescriptor("Test", properties);
    }

    public static final String getXml() {
        return "<project>\n" +
                "   <properties>\n" +
                "       <hudson.plugins.buildblocker.BuildBlockerProperty>\n" +
                "           <useBuildBlocker>true</useBuildBlocker>\n" +
                "           <blockingJobs>Build-iOS-App</blockingJobs>\n" +
                "           <blockLevel>GLOBAL</blockLevel>\n" +
                "           <scanQueueFor>DISABLED</scanQueueFor>\n" +
                "       </hudson.plugins.buildblocker.BuildBlockerProperty>\n" +
                "   </properties>\n" +
                "   <builders>\n" +
                "       <org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater plugin=\"build-name-setter@1.6.5\">\n" +
                "           <macroTemplate>Test iOS App #${BUILD_NUMBER} | ${APP_VERSION}</macroTemplate>\n" +
                "           <fromFile>false</fromFile>\n" +
                "           <fromMacro>true</fromMacro>\n" +
                "           <macroFirst>true</macroFirst>\n" +
                "       </org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater>\n" +
                "       <hudson.tasks.Shell>\n" +
                "           <command>export PLATFORM=iOS\n" +
                "               cd 'iOSTest-AppiumTests/src/scripts/'\n" +
                "               fab -f test.py initialize_env run_appium run_tests kill_appium\n" +
                "           </command>\n" +
                "       </hudson.tasks.Shell>\n" +
                "   </builders>\n" +
                "</project>";
    }

    public static final String getDSL() {
        return "job(\"Test\") {\n" +
                "   blockOn(\"Build-iOS-App\", {\n" +
                "       blockLevel(\"GLOBAL\")\n" +
                "       scanQueueFor(\"DISABLED\")\n" +
                "   }\n" +
                ")\n" +
                "   steps {\n" +
                "       buildNameUpdater {\n" +
                "           macroTemplate(\"Test iOS App #${BUILD_NUMBER} | ${APP_VERSION}\")\n" +
                "           fromFile(false)\n" +
                "           fromMacro(true)\n" +
                "           macroFirst(true)\n" +
                "       }\n" +
                "       shell(\"\"\"export PLATFORM=${platform}\n" +
                "cd \"iOSTest-AppiumTests/src/scripts/\"\n" +
                "fab -f build.py fetch_git build_app upload_app\n" +
                "\"\"\")\n" +
                "   }\n" +
                "}\n";
    }
}
