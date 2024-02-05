package Dissimulo;

public class StoredObject {
    private Object object;
    private Class<?> aClass;

    public StoredObject(Object object, Class<?> aClass) {
        this.object = object;
        this.aClass = aClass;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }
}
