/**
 * Facade: one simple entry point in front of a subsystem with many parts.
 *
 * The subsystem is unchanged and still reachable. The facade just adds a door
 * for the ninety percent of callers who want the default path.
 *
 * Run: npx tsx facade.ts
 */

class VideoFile {
  constructor(readonly name: string) {
    console.log(`  opened ${name}`);
  }
}

class Codec {
  constructor(readonly name: string) {}
}

const CodecFactory = {
  extract(_file: VideoFile): Codec {
    console.log("  detected codec: ogg");
    return new Codec("ogg");
  },
};

class VideoStream {
  constructor(readonly description: string) {}
}

const BitrateReader = {
  read(file: VideoFile, codec: Codec): VideoStream {
    console.log(`  decoded ${file.name} with ${codec.name}`);
    return new VideoStream(`decoded:${file.name}`);
  },
  convert(stream: VideoStream, target: Codec, kbps: number): VideoStream {
    console.log(`  converted to ${target.name} at ${kbps}kbps`);
    return new VideoStream(`${stream.description}->${target.name}`);
  },
};

const AudioMixer = {
  fix(stream: VideoStream): VideoStream {
    console.log("  normalized audio");
    return stream;
  },
};

const FileWriter = {
  write(_stream: VideoStream, extension: string): string {
    console.log(`  wrote output.${extension}`);
    return `output.${extension}`;
  },
};

/**
 * The facade. In TypeScript this is often just an exported function, which is
 * the same pattern without a class: a module with a small public surface.
 */
export function convertToMp4(sourcePath: string): string {
  const file = new VideoFile(sourcePath);
  const decoded = BitrateReader.read(file, CodecFactory.extract(file));
  const converted = BitrateReader.convert(decoded, new Codec("mp4"), 4000);
  return FileWriter.write(AudioMixer.fix(converted), "mp4");
}

console.log("--- through the facade: one line ---");
console.log("result:", convertToMp4("talk.ogg"));

console.log();
console.log("--- direct subsystem use: still available for the unusual case ---");
const file = new VideoFile("talk.ogg");
const decoded = BitrateReader.read(file, CodecFactory.extract(file));
const converted = BitrateReader.convert(decoded, new Codec("mp4"), 6000);
console.log("result:", FileWriter.write(AudioMixer.fix(converted), "mp4"));
