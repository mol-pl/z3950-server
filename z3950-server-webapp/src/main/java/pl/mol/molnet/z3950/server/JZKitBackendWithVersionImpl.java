package pl.mol.molnet.z3950.server;

import org.jzkit.service.z3950server.JZKitBackend;

/**
 * Nadpisuje pobranie wersji bo orginalny backend ma z tym problem.
 *
 * @author Paweł
 */
public class JZKitBackendWithVersionImpl extends JZKitBackend {

    @Override
    public String getVersion() {
        return "3.0.0";
    }
}
