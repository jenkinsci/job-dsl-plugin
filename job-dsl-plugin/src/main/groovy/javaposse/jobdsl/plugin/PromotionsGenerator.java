package javaposse.jobdsl.plugin;

import hudson.Util;
import hudson.XmlFile;
import hudson.model.ItemGroupMixIn;
import hudson.model.Items;
import hudson.model.AbstractItem;
import hudson.model.Job;
import hudson.util.AtomicFileWriter;
import hudson.util.IOException2;
import hudson.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import jenkins.model.Jenkins;

/**
 * Generate "promotions"-folder including the defined promotions with their config.xml's Code copied from
 * {@link ItemGroupMixIn#createProjectFromXML(String, InputStream)} and {@link AbstractItem#updateByXml(Source)}
 */
public class PromotionsGenerator {

    private String name;

    private String jobName;

    public PromotionsGenerator(String name, String jobName) {
        this.name = name;
        this.jobName = jobName;
    }

    public synchronized void createPromotionFromXML(InputStream xml) throws IOException {
        Jenkins jenkins = Jenkins.getInstance();
        jenkins.checkPermission(Job.CREATE);

        // place it as config.xml
        File configXml = Items.getConfigFile(getRootDir()).getFile();
        configXml.getParentFile().mkdirs();
        try {
            IOUtils.copy(xml, configXml);
        } catch (IOException e) {
            // if anything fails, delete the config file to avoid further confusion
            Util.deleteRecursive(configXml.getParentFile());
            throw e;
        }
    }

    public static File getRootDirFor(String jobName, String promotionName) {
        Jenkins jenkins = Jenkins.getInstance();
        return new File(new File(new File(new File(jenkins.getRootDir(), "jobs"), jobName), "promotions"),
                promotionName);
    }

    public void updateByXml(Source source) throws IOException {
        Jenkins jenkins = Jenkins.getInstance();
        jenkins.checkPermission(Job.CONFIGURE);
        XmlFile configXmlFile = Items.getConfigFile(getRootDir());
        AtomicFileWriter out = new AtomicFileWriter(configXmlFile.getFile());
        try {
            try {
                // this allows us to use UTF-8 for storing data,
                // plus it checks any well-formedness issue in the submitted
                // data
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.transform(source, new StreamResult(out));
                out.close();
            } catch (TransformerException e) {
                throw new IOException2("Failed to persist configuration.xml", e);
            }

            // try to reflect the changes by reloading
            new XmlFile(Items.XSTREAM, out.getTemporaryFile()).unmarshal(this);

            // if everything went well, commit this new version
            out.commit();
        } finally {
            out.abort(); // don't leave anything behind
        }
    }

    public File getRootDir() {
        return getRootDirFor(jobName, name);
    }

}
