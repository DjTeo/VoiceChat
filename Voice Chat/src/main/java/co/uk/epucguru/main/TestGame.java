package co.uk.epucguru.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import co.uk.epucguru.classes.VoiceChatClient;

public class TestGame extends Game {

	private Color backgroundColour = new Color(.9f, .9f, .9f, 1);
	private Server server;
	private Client client;
	private VoiceChatClient sender, reciever;
	private Batch batch;
	private BitmapFont font;
	
	public static void main(String... args){
		// Create game instance
		TestGame game = new TestGame();

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Voice Chat Test");
		config.useVsync(false);
		config.setResizable(false);
		
		new Lwjgl3Application(game, config);

		// Done
		System.gc();
		System.exit(0);
	}


	@Override
	public void create() {
		try{
			this.server = new Server(22050, 22050);
			server.bind(7777, 7777);
			server.start();
			
			this.client = new Client(22050, 22050);
			client.start();
			client.connect(5000, "localhost", 7777, 7777);
			
			this.sender = new VoiceChatClient(client.getKryo());
			this.reciever = new VoiceChatClient(server.getKryo());
			
			reciever.addReciever(server);
			
		}catch(Exception e){
			e.printStackTrace();
			Gdx.app.exit();
		}	
		
		
		// Other stuff for test
		batch = new SpriteBatch();
		font = new BitmapFont();
	}

	public void render(){
		Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.server.getConnections()[0].updateReturnTripTime();
		Gdx.graphics.setTitle("Voice Chat Test by James Billy - " + Gdx.graphics.getFramesPerSecond() + "fps, " + this.server.getConnections()[0].getReturnTripTime() + " ping.");
		
		batch.begin();
		font.setColor(Color.BLACK);
		// Test only
		if(Gdx.input.isKeyPressed(Keys.SPACE)){			
			
			// This line here is important, it will send audio when called.
			this.sender.update(this.client, Gdx.graphics.getDeltaTime());	
			
			// Test only
			font.draw(batch, "Now sending audio...", 10, 20);			
		}else{
			// Test only
			font.draw(batch, "Press SPACE to send audio to yourself!\n"
					+ "You may here some echo. This does not happen in a real game/app.\n"
					+ "There will also be latency. This is unavoidable, sorry.", 10, 60);
		}
		batch.end();
	}
}