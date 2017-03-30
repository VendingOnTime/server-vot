package com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.Validable;
import com.vendingontime.backend.models.machine.MachineLocation;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;

/**
 * Created by miguel on 28/3/17.
 */
public class NewMachineData implements Validable {
    private MachineLocation machineLocation; // Minimo 2, max 140
    private MachineType machineType;  // --
    private MachineState machineState; // --
    private String description; // Minimo 0, max 300

    @Override
    public String[] validate() {
        return null;
    }


}
