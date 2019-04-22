
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class PluginManager {

    private Map<String, Plugin> allPlugins;    //所有的Plugin
    private Set<Plugin> initIng;               //正在初始化的Plugin

    public PluginManager() {
        initIng = new LinkedHashSet<>(16);
        addTemp();  //临时数据
    }


    public void init() {
        Collection<Plugin> values = allPlugins.values();
        Iterator<Plugin> iterator = values.iterator();
        while (iterator.hasNext()) {
            Plugin next = iterator.next();
            initPlugin(next);
        }
    }

    private void initPlugin(Plugin plugin) throws RuntimeException {
        if (plugin.isInited()) {
            return;
        }

        if (initIng.contains(plugin)) {
            StringBuilder sb = new StringBuilder();
            Iterator<Plugin> iterator = initIng.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().name).append("->");
            }
            sb.append(plugin.name);
            throw new RuntimeException("发现循环依赖：" + sb.toString());
        }

        initIng.add(plugin);
        if (plugin.depends != null && plugin.depends.length > 0) {
            for (int i = 0; i < plugin.depends.length; i++) {
                Plugin dp = allPlugins.get(plugin.depends[i]);
                if (dp == null) {
                    throw new RuntimeException(plugin.name + " 缺少依赖：" + plugin.depends[i]);
                }
                initPlugin(dp);
            }
        }
        plugin.init();
        plugin.setInited();
        initIng.remove(plugin);
    }

    private void addTemp() {
        allPlugins = new HashMap(5);
        allPlugins.put("aa", new Plugin("aa", "bb", "cc"));
        allPlugins.put("bb", new Plugin("bb", "cc"));
        allPlugins.put("cc", new Plugin("cc", "ff"));
        allPlugins.put("ee", new Plugin("ee"));
        allPlugins.put("ff", new Plugin("ff", "ee"));

    }

    public static void main(String[] args) {
        new PluginManager().init();
    }

    public static class Plugin {
        public String name;         //组件别名，或者包名

        public String[] depends;    //依赖集合(包名)
        private boolean inited;     //标记是否init

        public Plugin(String name) {
            this.name = name;
        }

        public Plugin(String name, String... depends) {
            this.name = name;
            this.depends = depends;
        }

        public void init() {
            System.out.println("Plugin  : " + name + "inited: ");
        }

        void setInited() {
            this.inited = true;
        }

        boolean isInited() {
            return inited;
        }

    }
}
