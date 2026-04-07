package com.bloom.core.module;

import com.bloom.core.module.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        modules.add(new ToggleSprint());
        modules.add(new FpsDisplay());
        modules.add(new Coordinates());
        modules.add(new Zoom());
        modules.add(new CpsCounter());
        modules.add(new Keystrokes());
    }

    public List<Module> getModules() { return modules; }
}
