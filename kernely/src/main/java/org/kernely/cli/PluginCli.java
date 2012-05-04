/**
 * 
 */
package org.kernely.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipInputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepository;
import org.kernely.plugin.Descriptor;

import scala.actors.threadpool.Arrays;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

/**
 * The plugin cli to install, list, show and update the plugin system
 * 
 */
@SuppressWarnings("unchecked")
public class PluginCli {

	private static final String METADATA_DIRECTORY = ".metadata";

	/**
	 * The cli main method
	 * 
	 * @param args
	 *            the args of the cli, obviously
	 */
	public static void main(String[] args) {

		// read configuration
		try {
			URL resource = PluginCli.class.getClassLoader().getResource("core.xml");
			XMLConfiguration config = new XMLConfiguration(resource);
			String pluginDirectoryPath = config.getString("plugins.directory");
			String repository = config.getString("plugins.repository");
			String pluginMedataPath = pluginDirectoryPath + File.separator + METADATA_DIRECTORY;
			File metadataFile = new File(pluginMedataPath);
			if (args.length > 0) {
				if ("update".equals(args[0])) {
					update(repository, metadataFile);

				} else if ("list".equals(args[0])) {
					if (!metadataFile.exists()) {
						update(repository, metadataFile);
					}
					List<String> list = Arrays.asList(metadataFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".json");
						}
					}));
					if (list.size() == 0) {
						System.out.println("No plugins found");
					}
					List<List<String>> partition = Lists.partition(list, 3);
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb, Locale.US);
					String format = "%1$-20.20s";
					for (List<String> part : partition) {
						for (String plugin : part) {
							Descriptor loadManifest = loadManifest(pluginMedataPath+File.separator+plugin);
							formatter.format(format, loadManifest.name);
						}
						sb.append("\n");
					}
					System.out.println(sb.toString());

				} else if ("show".equals(args[0])) {

					if (args.length == 2) {
						Descriptor m = loadManifest(pluginMedataPath, args[1]);
						if (m != null) {

							StringBuilder sb = new StringBuilder();
							Formatter formatter = new Formatter(sb, Locale.US);
							String format = "%1$-15.15s : %2$-200.200s\n";
							formatter.format(format, "Name", m.name);
							formatter.format(format, "Version", m.version);
							formatter.format(format, "Download url", m.url);
							formatter.format(format, "Author", m.author);
							formatter.format(format, "Description", m.description);
							System.out.println(sb);
						} else {
							System.out.println("Cannot load plugin manifest for " + args[1]);
						}

					} else {
						System.out.println("Usage : show [name]");
					}

				} else if ("install".equals(args[0])) {
					Descriptor m = loadManifest(pluginMedataPath, args[1]);
					if (m != null) {
						final String pluginName = args[1] + "-" + m.version;
						final File pluginDirectory = new File(pluginDirectoryPath + File.separator + pluginName);
						if (pluginDirectory.exists()) {
							System.out.println("Plugin already installed");

						} else {
							try {
								final File dlFile = File.createTempFile(pluginName, "zip");
								try {
									final FileOutputStream stream = new FileOutputStream(dlFile);
									System.out.println("Try to load the plugin from " + m.url);
									AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
									Future<String> f;
									try {
										f = asyncHttpClient.prepareGet(m.url).execute(new AsyncHandler<String>() {
											ConsoleProgressMonitor downloadMonitor;
											@Override
											public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
												downloadMonitor.tick();
												bodyPart.writeTo(stream);
												return STATE.CONTINUE;
											}
											@Override
											public String onCompleted() throws Exception {
												downloadMonitor.end();
												pluginDirectory.mkdir();
												// unzip the file
												ZipInputStream zin = new ZipInputStream(Files.newInputStreamSupplier(dlFile).getInput());
												ZipUtil.extractToFolder(zin, pluginDirectory, new ConsoleProgressMonitor("Extracting"));

												System.out.println("Done! You can know restart your server.");
												return null;
											}

											@Override
											public STATE onHeadersReceived(HttpResponseHeaders arg0) throws Exception {
												System.out.println();
												downloadMonitor = new ConsoleProgressMonitor("Downloading");
												downloadMonitor.start();
												return STATE.CONTINUE;
											}

											@Override
											public STATE onStatusReceived(HttpResponseStatus arg0) throws Exception {
												return STATE.CONTINUE;
											}

											@Override
											public void onThrowable(Throwable arg0) {
												System.out.println("A problem occured while downloading " + pluginName);
											}

										});
										f.get();

									} catch (IOException e) {
										System.out.println("Cannot install plugin");
									} catch (InterruptedException e) {
										System.out.println("Cannot install plugin");
									} catch (ExecutionException e) {
										System.out.println("Cannot install plugin");
									} finally {
										asyncHttpClient.close();
									}
								} catch (IOException e2) {
									System.out.println("Cannot install plugin");
								}

							} catch (FileNotFoundException e1) {
								System.out.println("Cannot install plugin");
							} catch (IOException e) {
								System.out.println("Cannot install plugin");
							}
						}

					} else {
						System.out.println("Cannot load plugin manifest for " + args[1]);
					}
				}
			} else {
				System.out.println("Usage : update | list | show [name] | install [name]");
			}

		} catch (ConfigurationException e3) {
			System.out.println("Cannot load core.xml!");
		}

	}

	/**
	 * Update the metadata repository
	 * 
	 * @param repository
	 *            the repostory
	 * @param metadataFile
	 *            the metadata file
	 */
	private static void update(String repository, File metadataFile) {
		// update the list from the repository
		System.out.println("Udpating from " + repository);
		if (metadataFile.exists()) {
			Git wrap;
			try {
				wrap = Git.wrap(new FileRepository(metadataFile));
				wrap.pull();
			} catch (IOException e) {
				System.out.println("Cannot locate repository in " + metadataFile);
			}

		} else {
			System.out.print("-> Initializing data to " + metadataFile);
			CloneCommand clone = Git.cloneRepository();
			clone.setURI(repository);
			clone.setDirectory(metadataFile);
			clone.setBare(false);
			clone.call();
		}
		System.out.println("- Done!");
	}

	/**
	 * Load a plugin manifest
	 * 
	 * @param metadataPath
	 *            the path to look for
	 * @param pluginName
	 *            the plugin name to look for
	 * @return the loaded manifest or null if the manifest cannot be found.
	 */
	private static Descriptor loadManifest(String metadataPath, String pluginName) {
		File pluginFile = new File(metadataPath + File.separator + pluginName + ".json");
		if (pluginFile.exists()) {
			Gson g = new Gson();
			try {
				return g.fromJson(Files.newReader(pluginFile, Charsets.UTF_8), Descriptor.class);
			} catch (JsonSyntaxException e) {
				return null;
			} catch (JsonIOException e) {
				return null;
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {

			return null;
		}
	}
	/**
	 * The manifest
	 * @param file the file
	 * @return
	 */
	private static Descriptor loadManifest(String file){
		File pluginFile = new File(file);
		if (pluginFile.exists()) {
			Gson g = new Gson();
			try {
				return g.fromJson(Files.newReader(pluginFile, Charsets.UTF_8), Descriptor.class);
			} catch (JsonSyntaxException e) {
				return null;
			} catch (JsonIOException e) {
				return null;
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
