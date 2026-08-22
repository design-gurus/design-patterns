// Bridge: split two dimensions so they stop multiplying.
//
// 3 notification types x 3 channels would be 9 classes if both lived in one
// hierarchy. Here it is 3 + 3 = 6, and the gap widens as either side grows.
//
// Run: java BridgeDemo.java

public class BridgeDemo {
    public static void main(String[] args) {
        Channel email = new EmailChannel();
        Channel sms = new SmsChannel();

        // Any pairing works, and it is chosen at runtime.
        new AlertNotification(sms).send("+15551234", "disk is 91% full");
        new ReminderNotification(email).send("dev@example.com", "standup at 09:30");
        new DigestNotification(email).send("dev@example.com", "12 events overnight");

        // Swapping the channel does not touch the notification classes at all.
        new AlertNotification(email).send("oncall@example.com", "disk is 91% full");
    }
}

// The implementation side. Add a channel: one class, no notification changes.
interface Channel {
    void deliver(String to, String body);
}

class EmailChannel implements Channel {
    public void deliver(String to, String body) {
        System.out.println("email  -> " + to + ": " + body);
    }
}

class SmsChannel implements Channel {
    public void deliver(String to, String body) {
        System.out.println("sms    -> " + to + ": " + body.substring(0, Math.min(60, body.length())));
    }
}

// The abstraction side. Add a type: one class, no channel changes.
abstract class Notification {
    protected final Channel channel; // the bridge

    protected Notification(Channel channel) {
        this.channel = channel;
    }

    public abstract void send(String user, String text);
}

class AlertNotification extends Notification {
    AlertNotification(Channel channel) { super(channel); }

    public void send(String user, String text) {
        channel.deliver(user, "[ALERT] " + text);
    }
}

class ReminderNotification extends Notification {
    ReminderNotification(Channel channel) { super(channel); }

    public void send(String user, String text) {
        channel.deliver(user, "Reminder: " + text);
    }
}

class DigestNotification extends Notification {
    DigestNotification(Channel channel) { super(channel); }

    public void send(String user, String text) {
        channel.deliver(user, "Your daily digest\n  " + text);
    }
}
