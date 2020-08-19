/****************************************************************************
 * Copyright (C) from 2009 to Present EPAM Systems.
 *
 * This file is part of Indigo toolkit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package com.epam.indigo;

import com.sun.jna.Native;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;

public class IndigoInchi {
    public IndigoInchi(Indigo indigo) {
        loadLibrary(indigo.getUserSpecifiedPath());
        this.indigo = indigo;
    }

    public String version() {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiVersion());
    }

    public void resetOptions() {
        indigo.setSessionID();
        Indigo.checkResult(this, lib.indigoInchiResetOptions());
    }

    public IndigoObject loadMolecule(String inchi) {
        indigo.setSessionID();
        return new IndigoObject(indigo, Indigo.checkResult(this, lib.indigoInchiLoadMolecule(inchi)));
    }

    public String getInchi(IndigoObject molecule) {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiGetInchi(molecule.self));
    }

    public String getInchiKey(String inchi) {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiGetInchiKey(inchi));
    }

    public String getWarning() {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiGetWarning());
    }

    public String getLog() {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiGetLog());
    }

    public String getAuxInfo() {
        indigo.setSessionID();
        return Indigo.checkResultString(this, lib.indigoInchiGetAuxInfo());
    }

    private static String getPathToBinary(String path, String filename) {
        String dllpath = Indigo.getPlatformDependentPath();

        if (path == null) {
            String res = Indigo.extractFromJar(IndigoInchi.class, File.separator + dllpath, filename);
            if (res != null)
                return res;
            path = "lib";
        }
        path = path + File.separator + dllpath + File.separator + filename;
        try {
            return (new File(path)).getCanonicalPath();
        } catch (IOException e) {
            return path;
        }
    }

    private synchronized static void loadLibrary(String path) {
        if (lib != null)
            return;

        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_SUN_OS)
            lib = Native.load(getPathToBinary(path, "libindigo-inchi.so"), IndigoInchiLib.class);
        else if (SystemUtils.IS_OS_MAC)
            lib = Native.load(getPathToBinary(path, "libindigo-inchi.dylib"), IndigoInchiLib.class);
        else if (SystemUtils.IS_OS_WINDOWS)
            lib = Native.load(getPathToBinary(path, "indigo-inchi.dll"), IndigoInchiLib.class);
    }

    final Indigo indigo;
    static IndigoInchiLib lib;
}
