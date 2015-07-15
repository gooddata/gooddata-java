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

    protected final String title;
    protected final GoodData gd;

    protected Project project;

    protected String fact;
    protected Attribute attr;
    protected Metric metric;
    protected Report report;
    protected ReportDefinition reportDefinition;
    protected ScheduledMail scheduledMail;


    public AbstractGoodDataAT() {
        title = "sdktest " + new LocalDate() + " " + System.getenv("BUILD_NUMBER");
        gd = new GoodData(getProperty("host"), getProperty("login"), getProperty("pass"));
    }

    public static String getProperty(String name) {
        final String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("Environment variable " + name + " not found! Available variables: " +
                System.getenv().keySet());
    }

    @AfterSuite
    public void logout() throws Exception {
        if (gd != null) {
            gd.logout();
        }
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }
}
