package io.github.nanashithenameless.engrok.mixin;

import io.github.nanashithenameless.engrok.Engrok;
import io.github.nanashithenameless.engrok.initialization.LoadWorldInvoker;
import io.github.nanashithenameless.engrok.config.EngrokConfig;
import io.github.nanashithenameless.engrok.integrations.GitHubGists;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(MinecraftServer.class)
public class LoadWorldMixin implements LoadWorldInvoker {

	EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();

	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void loadWorld(CallbackInfo callback) {
		if (config.enabled) { //if mod enabled in mod menu

			int localPort = ((MinecraftServer)(Object) this).getServerPort();
			if(localPort == 0 || localPort == -1)
				localPort = 25565;
			//Engrok.LOGGER.warn("Port: " + localPort)
			initialization(localPort, config.regionSelect);
		}
	}

	@Override
	public void initialization(int localPort ,EngrokConfig.regionSelectEnum region)
	{
		if(!Engrok.tunnelOpen)
		{
			ngrokInit(localPort, region.getNgrokRegionCode());
		}
	}

	private void ngrokInit(int port, String regionCode) {
		//Defines a new threaded function to open the Ngrok tunnel, so that the "Open to LAN" button does not hitch - this thread runs in a separate process from the main game loop
		Engrok.canCommand = false;
		Thread thread = new Thread(() ->
		{
			if (config.ngrokAuthToken.equals("Insert your Ngrok auth token here") || config.ngrokAuthToken.isEmpty()) {
				// Check if authToken field has actually been changed, if not, print this text in chat
				Engrok.LOGGER.error("You need insert your Ngrok auth token in the config file in order for it to open a tunnel!\n The config file is located in the server folder, under config/engrok.json5\n You can do this in the mods folder, inside the Engrok mod config.\n You can obtain your Auth token from the Ngrok website after logging in.");
			} else {
				try {
					Engrok.LOGGER.info("Starting Ngrok Service...");

					if(regionCode == null)
						Engrok.LOGGER.info("Using ngrok automatic point-of-presence selection.");
					else
						Engrok.LOGGER.info("Pinning ngrok to region code " + regionCode + ".");

					final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
							.withAuthToken(config.ngrokAuthToken)
							.withConfigPath(createRuntimeNgrokConfig(regionCode))
							.withoutMonitoring()
							.build();

					Engrok.ngrokClient = new NgrokClient.Builder()
							.withJavaNgrokConfig(javaNgrokConfig)
							.build();

					final CreateTunnel createTunnel = new CreateTunnel.Builder()
							.withProto(Proto.TCP)
							.withAddr(port)
							.build();

					final Tunnel tunnel = Engrok.ngrokClient.connect(createTunnel);

					//Engrok.LOGGER.info(tunnel.getPublicUrl());

					var ngrok_url = tunnel.getPublicUrl().substring(6);
					Engrok.LOGGER.info("Ngrok Service Initiated Successfully!");
					Engrok.LOGGER.info("Your server IP is - " + ngrok_url);
					new GitHubGists().setIpGist(ngrok_url);

					Engrok.tunnelOpen = true;
					Engrok.canCommand = true;

				} catch (Exception error) {
					error.printStackTrace();
					Engrok.LOGGER.warn(error.getMessage());
					Engrok.LOGGER.error("Ngrok Service Initiation Failed!");
					//ngrokInitiated = false;
					throw new RuntimeException("Ngrok Service Failed to Start" + error.getMessage());
				}
			}
		});

		// This starts the thread defined above
		thread.start();
	}

	private Path createRuntimeNgrokConfig(String regionCode) throws IOException {
		// java-ngrok's default config for ngrok v3 still seeds "region: us".
		// Writing our own config keeps AUTO truly automatic and lets us pass newer raw PoP codes.
		Path runtimeConfigPath = Files.createTempFile("engrok-ngrok-", ".yml");
		runtimeConfigPath.toFile().deleteOnExit();

		StringBuilder configBuilder = new StringBuilder("version: 2\n");
		if(regionCode != null)
			configBuilder.append("region: ").append(regionCode).append('\n');

		Files.writeString(runtimeConfigPath, configBuilder.toString(), StandardCharsets.UTF_8);
		return runtimeConfigPath;
	}
}
