/**
 * Bridge: split two dimensions so they stop multiplying.
 *
 * 3 notification types x 3 channels would be 9 classes if both lived in one
 * hierarchy. Here it is 3 + 3 = 6, and the gap widens as either side grows.
 *
 * Run: npx tsx bridge.ts
 */

// The implementation side. In TypeScript a one-method interface can simply be
// a function type, which makes the bridge almost invisible.
type Channel = (to: string, body: string) => void;

const emailChannel: Channel = (to, body) => console.log(`email  -> ${to}: ${body}`);
const smsChannel: Channel = (to, body) => console.log(`sms    -> ${to}: ${body.slice(0, 60)}`);

// The abstraction side.
abstract class Notification {
  constructor(protected readonly channel: Channel) {} // the bridge
  abstract send(user: string, text: string): void;
}

class AlertNotification extends Notification {
  send(user: string, text: string): void {
    this.channel(user, `[ALERT] ${text}`);
  }
}

class ReminderNotification extends Notification {
  send(user: string, text: string): void {
    this.channel(user, `Reminder: ${text}`);
  }
}

class DigestNotification extends Notification {
  send(user: string, text: string): void {
    this.channel(user, `Your daily digest\n  ${text}`);
  }
}

new AlertNotification(smsChannel).send("+15551234", "disk is 91% full");
new ReminderNotification(emailChannel).send("dev@example.com", "standup at 09:30");
new DigestNotification(emailChannel).send("dev@example.com", "12 events overnight");

// Swapping the channel does not touch the notification classes at all.
new AlertNotification(emailChannel).send("oncall@example.com", "disk is 91% full");
