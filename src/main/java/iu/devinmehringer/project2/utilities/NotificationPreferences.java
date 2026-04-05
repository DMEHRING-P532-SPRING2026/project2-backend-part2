package iu.devinmehringer.project2.utilities;

public class NotificationPreferences {
    private boolean console;
    private boolean inApp;
    private boolean email;

    public boolean isConsole() { return console; }
    public void setConsole(boolean console) { this.console = console; }

    public boolean isInApp() { return inApp; }
    public void setInApp(boolean inApp) { this.inApp = inApp; }

    public boolean isEmail() { return email; }
    public void setEmail(boolean email) { this.email = email; }

    public boolean isEnabled(Class<?> notifierClass) {
        if (notifierClass == ConsoleNotifier.class) return isConsole();
        if (notifierClass == InAppNotifier.class)   return isInApp();
        if (notifierClass == EmailNotifier.class)   return isEmail();
        return false;
    }
}