import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ExcelTableWriter;
import genepi.io.table.writer.ITableWriter;
import genepi.io.text.LineReader;
import genepi.io.text.LineWriter;
import picocli.CommandLine;
import picocli.CommandLine.Option;

//usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS jcenter,jfrog-genepi-maven=https://genepi.jfrog.io/artifactory/maven
//DEPS info.picocli:picocli:4.5.0
//DEPS genepi:genepi-io:1.0.12

public class TabToRsIndex implements Callable<Integer> {

	@Option(names = "--input", description = "input tab file", required = true)
	private String input;

	@Option(names = "--output", description = "output file", required = true)
	private String output;

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public static void main(String... args) {
		int exitCode = new CommandLine(new TabToRsIndex()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		LineReader reader = new LineReader(input);

		LineWriter writer = new LineWriter(output);


		while (reader.next()) {

			String tiles[] = reader.get().split("\t");
			String id = tiles[2];

			if (id.startsWith("rs")) {
				String contig = TabToRsIndex.getContig(id);
				int position = TabToRsIndex.getPosition(id);
				writer.write(contig + "\t" + position + "\t" + tiles[0] + "\t" + tiles[1] + "\t"
						+ tiles[3] + "\t" + tiles[4]);
			}
		}

		writer.close();
		reader.close();

		return 0;
	}


	public static String getContig(String rsID) {
		if (rsID.length() > 10) {
			// TODO: count zeros --> rs1, rs10, ...
			String position = rsID.substring(4);
			int count = countCharacter(position, '0');
			return rsID.substring(0, 4) + sequence('0', count);
		} else {
			String position = rsID.substring(2);
			int count = countCharacter(position, '0');
			return "rs" + sequence('0', count);
		}
	}

	public static int getPosition(String rsID) {
		if (rsID.length() > 10) {
			return Integer.parseInt(rsID.substring(4));
		} else {
			return Integer.parseInt(rsID.substring(2));
		}
	}

	public static int countCharacter(String string, char character) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) != character) {
				break;
			}
			count++;
		}
		return count;
	}

	public static String sequence(char character, int count) {
		String result = "";
		for (int i = 0; i < count; i++) {
			result += character;
		}
		return result;
	}

}
