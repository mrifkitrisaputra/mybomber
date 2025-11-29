// package com.mygg.render;

// import com.mygg.core.InputHandler;
// import com.mygg.entities.Player;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.image.Image;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

// public class SpritePreview extends Application {

//     @Override
//     public void start(Stage stage) {

//         SpriteLoader loader = new SpriteLoader();

//         // Load sheet 1
//         loader.load("/com/mygg/assets/player/sprites.json",
//                     "/com/mygg/assets/player/spritesheet.png");

//         // Load sheet 2
//         loader.load("/com/mygg/assets/player/set2.json",
//                     "/com/mygg/assets/player/spritesheet2.png");

//         Player player = new Player();

//         // Load anim sets
//         loadAnim(player, loader, "idle_down", 1);
//         loadAnim(player, loader, "idle_up", 0x1);
//         loadAnim(player, loader, "idle_left", 1);
//         loadAnim(player, loader, "idle_right", 1);

//         loadAnim(player, loader, "walk_down", 4);
//         loadAnim(player, loader, "walk_right", 4);
//         loadAnim(player, loader, "walk_up", 4);
//         loadAnim(player, loader, "walk_left", 3);

//         loadAnim(player, loader, "place_bom", 4);
//         loadAnim(player, loader, "death", 4);

//         InputHandler input = new InputHandler();

//         GameCanvas canvas = new GameCanvas(player, input);
//         Scene scene = new Scene(new StackPane(canvas));

//         input.attach(scene);

//         stage.setTitle("Bomberman Animation Test");
//         stage.setScene(scene);
//         stage.show();
//     }

//     private void loadAnim(Player p, SpriteLoader loader, String base, int count) {
//         Image[] arr = new Image[count];

//         for (int i = 1; i <= count; i++) {
//             arr[i - 1] = loader.get(base + i);
//         }

//         p.addAnim(base, new SpriteAnimation(arr));
//     }

//     public static void main(String[] args) {
//         launch();
//     }
// }
