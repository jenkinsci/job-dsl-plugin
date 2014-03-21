package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Sets

import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView;

import java.util.logging.Level
import java.util.logging.Logger

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.getName());
    private static final Map<ViewType, Class<? extends View>> VIEW_TYPE_MAPPING = [
            (null): ListView.class,
            (ViewType.ListView): ListView.class,
            (ViewType.BuildPipelineView): BuildPipelineView.class,
            (ViewType.NestedView): NestedView.class,
    ]

    JobManagement jm;
    Set<Job> referencedJobs
    Set<View> referencedViews
    List<String> queueToBuild

    public JobParent() {
        referencedJobs = Sets.newLinkedHashSet()
        referencedViews = Sets.newLinkedHashSet()
        queueToBuild = Lists.newArrayList()
    }

    public Job job(Map<String, Object> arguments=[:], Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Job job = new Job(jm, arguments)

        // Configure with what we have already
        job.with(closure)

        // Save jobs, so that we know what to extract XML from
        referencedJobs << job

        // This job can have .configure { } called on
        return job
    }

    public View view(Map<String, Object> arguments=[:], Closure closure) {
        Class<? extends View> viewClass = VIEW_TYPE_MAPPING[arguments['type'] as ViewType]
        View view = viewClass.newInstance()
        view.with(closure)
        referencedViews << view

        // This view can have .configure { } called on
        return view
    }

    /**
     * Schedule a job to be run later. Validation of the job name isn't done until after the DSL has run.
     * @param jobName
     * @return
     */
    public queue(String jobName) {
        queueToBuild << jobName
    }

    /**
     * Schedule a job to be run later.
     * @param jobName
     * @return
     */
    public queue(Job job) {
        // TODO Consider lazily evaluating in case some Closure sets the name
        Preconditions.checkArgument(job.name as Boolean)
        queueToBuild << job.name
    }

    public InputStream streamFileFromWorkspace(String filePath) throws IOException {
        Preconditions.checkArgument(filePath as Boolean)
        return jm.streamFileInWorkspace(filePath);
    }

    public String readFileFromWorkspace(String filePath) throws IOException {
        Preconditions.checkArgument(filePath as Boolean)
        return jm.readFileInWorkspace(filePath);
    }
}
