package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.jobfilter.Status
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class SectionedViewSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final SectionedView view = new SectionedView(jobManagement, 'test')

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        compareXML(getXml('SectionedViewSpec-defaults.xml'), xml).similar()
    }

    def 'minimal job graphs section'() {
        when:
        view.sections {
            jobGraphs {
                name('test')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-minimalJobGraphsSection.xml'), view.xml).similar()
    }

    def 'minimal list view section'() {
        when:
        view.sections {
            listView {
                name('test')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-minimalListViewSection.xml'), view.xml).similar()
    }

    def 'complex list view section'() {
        when:
        view.sections {
            listView {
                name('test')
                width('HALF')
                alignment('LEFT')
                jobs {
                    name 'foo'
                    regex 'test-.*'
                }
                jobFilters {
                    status {
                        status(Status.UNSTABLE)
                    }
                }
                columns {
                    status()
                    name()
                }
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-complexListViewSection.xml'), view.xml).similar()
    }

    def 'minimal test result section'() {
        when:
        view.sections {
            testResult {
                name('test')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-minimalTestResultSection.xml'), view.xml).similar()
    }

    def 'minimal text section'() {
        when:
        view.sections {
            text {
                name('test')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-minimalTextSection.xml'), view.xml).similar()
    }

    def 'complex text section'() {
        when:
        view.sections {
            text {
                name('test')
                style('INFO')
                text('lorem ipsum')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-complexTextSection.xml'), view.xml).similar()
    }

    def 'minimal view list section'() {
        when:
        view.sections {
            viewListing {
                name('test')
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-minimalViewListSection.xml'), view.xml).similar()
    }

    def 'complex view list section'() {
        when:
        view.sections {
            viewListing {
                name('test')
                view('view-a')
                view('view-b')
                views('view-c', 'view-d')
                columns(2)
            }
        }

        then:
        compareXML(getXml('SectionedViewSpec-complexViewListSection.xml'), view.xml).similar()
    }

    private static String getXml(String resourceName) {
        SectionedViewSpec.getResourceAsStream(resourceName).getText('UTF-8')
    }
}
