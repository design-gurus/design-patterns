// Facade: one simple entry point in front of a subsystem with many parts.
//
// The subsystem is unchanged and still reachable. The facade just adds a door
// for the ninety percent of callers who want the default path.
//
// Run: java FacadeDemo.java

public class FacadeDemo {
    public static void main(String[] args) {
        System.out.println("--- through the facade: one line ---");
        String result = new VideoConverter().convertToMp4("talk.ogg");
        System.out.println("result: " + result);

        System.out.println();
        System.out.println("--- direct subsystem use: still available for the unusual case ---");
        VideoFile file = new VideoFile("talk.ogg");
        Codec source = CodecFactory.extract(file);
        VideoStream decoded = BitrateReader.read(file, source);
        VideoStream converted = BitrateReader.convert(decoded, new Mp4Codec(), 6_000);
        System.out.println("result: " + FileWriter.write(AudioMixer.fix(converted), "mp4"));
    }
}

// The facade. Note it returns our own types and holds no business rules.
final class VideoConverter {
    String convertToMp4(String sourcePath) {
        VideoFile file = new VideoFile(sourcePath);
        Codec source = CodecFactory.extract(file);
        VideoStream decoded = BitrateReader.read(file, source);
        VideoStream converted = BitrateReader.convert(decoded, new Mp4Codec(), 4_000);
        return FileWriter.write(AudioMixer.fix(converted), "mp4");
    }
}

// The subsystem. Powerful, and more than most callers want to think about.
class VideoFile {
    final String name;
    VideoFile(String name) {
        this.name = name;
        System.out.println("  opened " + name);
    }
}

interface Codec {
    String name();
}

class OggCodec implements Codec {
    public String name() { return "ogg"; }
}

class Mp4Codec implements Codec {
    public String name() { return "mp4"; }
}

class CodecFactory {
    static Codec extract(VideoFile file) {
        System.out.println("  detected codec: ogg");
        return new OggCodec();
    }
}

class VideoStream {
    final String description;
    VideoStream(String description) { this.description = description; }
}

class BitrateReader {
    static VideoStream read(VideoFile file, Codec codec) {
        System.out.println("  decoded " + file.name + " with " + codec.name());
        return new VideoStream("decoded:" + file.name);
    }

    static VideoStream convert(VideoStream stream, Codec target, int kbps) {
        System.out.println("  converted to " + target.name() + " at " + kbps + "kbps");
        return new VideoStream(stream.description + "->" + target.name());
    }
}

class AudioMixer {
    static VideoStream fix(VideoStream stream) {
        System.out.println("  normalized audio");
        return stream;
    }
}

class FileWriter {
    static String write(VideoStream stream, String extension) {
        System.out.println("  wrote output." + extension);
        return "output." + extension;
    }
}
