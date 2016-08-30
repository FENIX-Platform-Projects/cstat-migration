package org.fao.ess.cstat.migration.logic;


import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;

@Singleton
public class LogicFactory {
    static WeldContainer weld = new Weld().initialize();

    @Inject Instance<Logic> logicInstance;

    public static Logic getInstance(String command) {
        Iterator<Logic> logicIterator = weld.instance().select(Logic.class).iterator();
        if (command!=null && logicIterator!=null)
            while (logicIterator.hasNext()) {
                Logic logic = logicIterator.next();
                Command commandAnnotation = logic.getClass().getAnnotation(Command.class);
                if (commandAnnotation!=null && command.trim().equalsIgnoreCase(commandAnnotation.value()))
                    return logic;
            }
        return null;
    }
}
