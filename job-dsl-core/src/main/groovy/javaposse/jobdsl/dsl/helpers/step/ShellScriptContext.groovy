package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ShellScriptContext implements Context {
    List<String> blocks = []

    /**
     * Adds a block to the shell script.
     *
     * @param block block to add
     */
    void block(String block) {
        blocks << block
    }

    /**
     * Adds blocks to the shell script.
     *
     * @param blocks blocks to add
     */
    void block(List<String> blocks) {
        this.blocks.addAll(blocks)
    }
}
