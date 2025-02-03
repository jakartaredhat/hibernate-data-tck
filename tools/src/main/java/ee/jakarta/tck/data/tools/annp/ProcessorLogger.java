package ee.jakarta.tck.data.tools.annp;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

/**
 * A logger for the annotation processor that uses the ProcessingEnvironment to print messages
 */
class ProcessorLogger {
    private static ProcessorLogger instance;
    private ProcessingEnvironment env;

    ProcessorLogger(ProcessingEnvironment env) {
        this.env = env;
        instance = this;
    }

    public static ProcessorLogger getInstance() {
        return instance;
    }

    void warn(String message) {
        print(Diagnostic.Kind.WARNING, message);
    }

    void info(String message) {
        print(Diagnostic.Kind.NOTE, message);
    }

    void debug(String message) {
        print(Diagnostic.Kind.NOTE, message);
    }

    private void print(Diagnostic.Kind kind, String message) {
        env.getMessager().printMessage(kind, message);
    }
}