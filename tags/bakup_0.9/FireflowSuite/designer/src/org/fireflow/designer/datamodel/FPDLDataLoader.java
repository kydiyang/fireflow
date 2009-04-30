/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class FPDLDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/x-fireflow+xml";
    private static final long serialVersionUID = 1L;

    public FPDLDataLoader() {
        super("org.fireflow.designer.datamodel.FPDLDataObject");
//        System.out.println("================是否动用了dataloader?");
    }

    @Override
    protected String defaultDisplayName() {
        return NbBundle.getMessage(FPDLDataLoader.class, "LBL_FPDL_loader_name");
    }

    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new FPDLDataObject(primaryFile, this);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
