"""Bridge: split two dimensions so they stop multiplying.

3 notification types x 3 channels would be 9 classes if both lived in one
hierarchy. Here it is 3 + 3 = 6, and the gap widens as either side grows.

Run: python bridge.py
"""

from abc import ABC, abstractmethod
from typing import Protocol


class Channel(Protocol):
    def deliver(self, to: str, body: str) -> None: ...


class EmailChannel:
    def deliver(self, to: str, body: str) -> None:
        print(f"email  -> {to}: {body}")


class SmsChannel:
    def deliver(self, to: str, body: str) -> None:
        print(f"sms    -> {to}: {body[:60]}")


class Notification(ABC):
    def __init__(self, channel: Channel):
        self.channel = channel  # the bridge

    @abstractmethod
    def send(self, user: str, text: str) -> None: ...


class AlertNotification(Notification):
    def send(self, user: str, text: str) -> None:
        self.channel.deliver(user, f"[ALERT] {text}")


class ReminderNotification(Notification):
    def send(self, user: str, text: str) -> None:
        self.channel.deliver(user, f"Reminder: {text}")


class DigestNotification(Notification):
    def send(self, user: str, text: str) -> None:
        self.channel.deliver(user, f"Your daily digest\n  {text}")


def main() -> None:
    email, sms = EmailChannel(), SmsChannel()

    AlertNotification(sms).send("+15551234", "disk is 91% full")
    ReminderNotification(email).send("dev@example.com", "standup at 09:30")
    DigestNotification(email).send("dev@example.com", "12 events overnight")

    # Swapping the channel does not touch the notification classes at all.
    AlertNotification(email).send("oncall@example.com", "disk is 91% full")


if __name__ == "__main__":
    main()
