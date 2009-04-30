package org.fireflow.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.fireflow.model.WorkflowProcess;


/**
 * @author Chennieyun
 */
public interface IFPDLParser extends FPDLNames{

    /** Parse the given InputStream into a Package object.

        @param in The InputStream
        @throws IOException Any I/O Exception
        @throws FPDLParserException Any parser exception
    */

    public WorkflowProcess parse(InputStream in) throws IOException, FPDLParserException;
//    public WorkflowProcess parse(Reader in) throws IOException, FPDLParserException;

}
