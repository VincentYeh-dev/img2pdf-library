package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.parser.core.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.FileChecker;

import java.io.File;
import java.io.IOException;

public class ConvertActionParser extends ActionParser<ConvertAction> {
    private static final String DEFAULT_TEMP_FOLDER = ".org.vincentyeh.IMG2PDF.tmp";
    private static final String DEFAULT_MAX_MEMORY_USAGE = "50MB";

    private final Options options = new Options();
    private final CheckHelpParser parser;

    @Override
    public ConvertAction parse(String[] arguments) throws ParseException, HandledException {
        CommandLine cmd = parser.parse(options, arguments);

        boolean open_when_complete = cmd.hasOption("open_when_complete");
        boolean overwrite_output = cmd.hasOption("overwrite");
        String[] str_sources = cmd.getOptionValues("tasklist_source");
        File tempFolder = getTempFolder(cmd);

        try {
            FileChecker.makeDirsIfNotExists(tempFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandledException(e, getClass());
        }

        long maxMainMemoryBytes;
        try {
            maxMainMemoryBytes = getMaxMemoryBytes(cmd);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }


        File[] tasklist_sources;
        try {
            tasklist_sources = verifyFiles(str_sources);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }
        return new ConvertAction(tempFolder, maxMainMemoryBytes, tasklist_sources, open_when_complete, overwrite_output);
    }

    private long getMaxMemoryBytes(CommandLine cmd) {
        return BytesSize.parseString(cmd.getOptionValue("memory_max_usage", DEFAULT_MAX_MEMORY_USAGE)).getBytes();

    }

    private File getTempFolder(CommandLine cmd) {
        return new File(cmd.getOptionValue("temp_folder", DEFAULT_TEMP_FOLDER)).getAbsoluteFile();
    }

    public ConvertActionParser() {
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "convert.help");

        Option opt_tasklist_source = MultiLanguageOptionFactory.getArgumentOption("lsrc", "tasklist_source", "convert.arg.tasklist_source.help");
        opt_tasklist_source.setRequired(true);

        Option opt_open_when_complete = MultiLanguageOptionFactory.getOption("o", "open_when_complete", "convert.arg.open_when_complete.help");

        Option opt_overwrite = MultiLanguageOptionFactory.getOption("ow", "overwrite", "convert.arg.overwrite_output.help");

        Option opt_tmp_folder = MultiLanguageOptionFactory.getArgumentOption("tmp", "temp_folder", "convert.arg.tmp_folder.help", ".org.vincentyeh.IMG2PDF.tmp");
        Option opt_max_memory_usage = MultiLanguageOptionFactory.getArgumentOption("mx", "memory_max_usage", "convert.arg.memory_max_usage.help", "50MB");

        options.addOption(opt_help);
        options.addOption(opt_tasklist_source);
        options.addOption(opt_tmp_folder);
        options.addOption(opt_max_memory_usage);
        options.addOption(opt_open_when_complete);
        options.addOption(opt_overwrite);

        parser = new CheckHelpParser(opt_help);

        Option opt_mode = new Option("m", "mode", true, "mode");
        options.addOption(opt_mode);

    }

}
