"""Facade: one simple entry point in front of a subsystem with many parts.

The subsystem is unchanged and still reachable. The facade just adds a door for
the ninety percent of callers who want the default path.

Run: python facade.py
"""


class VideoFile:
    def __init__(self, name: str):
        self.name = name
        print(f"  opened {name}")


class Codec:
    def __init__(self, name: str):
        self.name = name


class CodecFactory:
    @staticmethod
    def extract(file: VideoFile) -> Codec:
        print("  detected codec: ogg")
        return Codec("ogg")


class VideoStream:
    def __init__(self, description: str):
        self.description = description


class BitrateReader:
    @staticmethod
    def read(file: VideoFile, codec: Codec) -> VideoStream:
        print(f"  decoded {file.name} with {codec.name}")
        return VideoStream(f"decoded:{file.name}")

    @staticmethod
    def convert(stream: VideoStream, target: Codec, kbps: int) -> VideoStream:
        print(f"  converted to {target.name} at {kbps}kbps")
        return VideoStream(f"{stream.description}->{target.name}")


class AudioMixer:
    @staticmethod
    def fix(stream: VideoStream) -> VideoStream:
        print("  normalized audio")
        return stream


class FileWriter:
    @staticmethod
    def write(stream: VideoStream, extension: str) -> str:
        print(f"  wrote output.{extension}")
        return f"output.{extension}"


# The facade. It sequences calls and holds no business rules.
class VideoConverter:
    def convert_to_mp4(self, source_path: str) -> str:
        file = VideoFile(source_path)
        source_codec = CodecFactory.extract(file)
        decoded = BitrateReader.read(file, source_codec)
        converted = BitrateReader.convert(decoded, Codec("mp4"), 4000)
        return FileWriter.write(AudioMixer.fix(converted), "mp4")


def main() -> None:
    print("--- through the facade: one line ---")
    print("result:", VideoConverter().convert_to_mp4("talk.ogg"))

    print()
    print("--- direct subsystem use: still available for the unusual case ---")
    file = VideoFile("talk.ogg")
    decoded = BitrateReader.read(file, CodecFactory.extract(file))
    converted = BitrateReader.convert(decoded, Codec("mp4"), 6000)
    print("result:", FileWriter.write(AudioMixer.fix(converted), "mp4"))


if __name__ == "__main__":
    main()
