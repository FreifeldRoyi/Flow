package org.freifeld.flow.plumber.implementations.pipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.freifeld.flow.plumber.Flow;
import org.freifeld.flow.plumber.Source;
import org.freifeld.flow.plumber.implementations.sinks.ReturnSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author royif
 * @since 18/06/16
 */
public class AsyncPipeTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPipeTest.class);

	@Test(groups = { "async" }, description = "Tests a simple run of the AsyncPipe")
	public void asyncPipe_simpleRun() throws InterruptedException
	{
		Source<JsonElement> fileSource = new Source<JsonElement>()
		{
			@Override
			public void start()
			{
				List<String> fileNames = Arrays.asList("airlines.json", "airports.json");
				List<JsonElement> objects = fileNames.stream().map(filename ->
				{
					List<JsonElement> elements = new LinkedList<>();
					try
					{
						String absFileName = getClass().getClassLoader().getResource(filename).getFile();
						String strJson = new String(Files.readAllBytes(Paths.get(absFileName)));
						JsonParser parser = new JsonParser();
						JsonElement json = parser.parse(strJson);
						JsonArray rows = json.getAsJsonObject().getAsJsonArray("rows");
						rows.forEach(elements::add);
					}
					catch (NullPointerException | IOException e)
					{
						LOGGER.error("Could not read path {}", filename, e);
						fail("Could not read path " + filename, e);
					}
					return elements;
				}).flatMap(Collection::stream).collect(Collectors.toList());
				this.pump("*", objects);
			}
		};

		AsyncPipe<JsonElement, String> asyncPipeUnderTest = new AsyncPipe<JsonElement, String>()
		{
			@Override
			protected String apply(JsonElement data)
			{
				String toReturn = "";
				if (!data.isJsonNull())
				{
					JsonObject jsonObj = data.getAsJsonObject();
					toReturn = Stream.of(jsonObj.get("name"), jsonObj.get("Name")).filter(Objects::nonNull).findFirst().get().getAsString();
				}
				return toReturn;
			}
		};

		ReturnSink<String> returnSink = new ReturnSink<>("retSink", null, true, true);

		Flow<JsonElement, String> flow = new Flow.Plumber<JsonElement, String>().from(fileSource).pipe(asyncPipeUnderTest).to(returnSink).build();

		flow.start();
		flow.close();
		assertTrue(returnSink.getStuff().size() > 0);
	}
}