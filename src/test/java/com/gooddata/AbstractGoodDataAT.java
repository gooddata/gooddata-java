package com.gooddata;

import com.gooddata.md.Attribute;
import com.gooddata.md.Metric;
import com.gooddata.md.ScheduledMail;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import org.joda.time.LocalDate;
import org.testng.annotations.AfterSuite;

/**
 * Parent for acceptance tests
 */
public abstract class AbstractGoodDataAT {

    protected static final String title =
            "sdktest " + new LocalDate() + " " + System.getenv("BUILD_NUMBER");

    protected static final GoodData gd =
            new GoodData(getProperty("host"), getProperty("login"), getProperty("pass"));

    protected static Project project;

    protected static String fact;
    protected static Attribute attr;
    protected static Metric metric;
    protected static Report report;
    protected static ReportDefinition reportDefinition;
    protected static ScheduledMail scheduledMail;

    public static String getProperty(String name) {
        final String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("Environment variable " + name + " not found! Available variables: " +
                System.getenv().keySet());
    }

    @AfterSuite
    public static void removeProjectAndLogout() {
        if (project != null) {
            gd.getProjectService().removeProject(project);
        }
        gd.logout();
    }
}
