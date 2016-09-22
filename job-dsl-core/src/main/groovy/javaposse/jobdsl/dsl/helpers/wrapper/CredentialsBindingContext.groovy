package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('org.jenkinsci.plugins.credentialsbinding.MultiBinding')
class CredentialsBindingContext extends AbstractExtensibleContext {
    final List<Node> nodes = []

    CredentialsBindingContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Copies the file given in the credentials to a temporary location, then sets the variable to that location.
     */
    void file(String variable, String credentials) {
        addSimpleBinding('File', variable, credentials)
    }

    /**
     * Sets a variable to the text given in the credentials.
     */
    void string(String variable, String credentials) {
        addSimpleBinding('String', variable, credentials)
    }

    /**
     * Sets a variable to the username and password given in the credentials, separated by a colon (:).
     */
    void usernamePassword(String variable, String credentials) {
        addSimpleBinding('UsernamePassword', variable, credentials)
    }

    /**
     * Sets one variable to the username and one variable to the password given in the credentials.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'credentials-binding', minimumVersion = '1.3')
    void usernamePassword(String userVariableName, String passwordVariableName, String credentials) {
        nodes << new NodeBuilder().'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding' {
            credentialsId(credentials)
            usernameVariable(userVariableName)
            passwordVariable(passwordVariableName)
        }
    }

    /**
     * Unpacks the ZIP file given in the credentials to a temporary directory, then sets the variable to that location.
     */
    void zipFile(String variable, String credentials) {
        addSimpleBinding('ZipFile', variable, credentials)
    }

    private void addSimpleBinding(String type, String variableName, String credentials) {
        nodes << new NodeBuilder()."org.jenkinsci.plugins.credentialsbinding.impl.${type}Binding" {
            variable(variableName)
            credentialsId(credentials)
        }
    }

    @Override
    protected void addExtensionNode(Node node) {
        nodes << node
    }
}
