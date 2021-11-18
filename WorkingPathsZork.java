package com.company;

import javax.crypto.spec.IvParameterSpec;
import java.io.PrintStream;
import java.util.Scanner;
import static com.company.ZorkProject.so;
import static com.company.ZorkProject.scan;

public class ZorkProject {

    static PrintStream so = System.out;
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        com.company.Player player = new com.company.Player();
        com.company.PathFactory factory = new com.company.PathFactory();
        com.company.Maze maze = new com.company.Maze(factory);
        maze.createPath();
        com.company.Game game = new com.company.Game(player, maze);

        so.println("Welcome to the Legend of Zorka!");

        while (game.stillPlaying) {
            game.intro();
            game.current.optional();
            String[] instruc = game.inputPlayer();
            game.parsing(instruc);
            game.update();
        }

        if(!player.health) {
            so.println("You died..... Game Over!");
        }
        else if (maze.victory) {
            so.println("You defeated the evil plaguing the land!");
            so.println("All Hail the new Hero!");
        }

        so.println("Thank you for playing!");

    }
}

class Game {
    boolean stillPlaying = true;
    com.company.Maze currGame;
    com.company.Player player1;
    public com.company.Room current;

    Game(com.company.Player player1, com.company.Maze currGame) {
        this.player1 = player1;
        this.currGame = currGame;
        current = currGame.room19;
    }

    public void intro() {
        so.printf("\nAction Score: %d\n", player1.score);
        so.printf("\n%s\n", current.getName());
        so.printf("%s\n", current.getDescrip());
    }

    public String[] inputPlayer() {
        so.print(">> ");
        String raw = scan.nextLine();
        player1.score += 1;
        return raw.split(" ");
    }

    public void parsing(String[] word_list) {
        if (word_list[0].equalsIgnoreCase("Fight") || word_list[0].equalsIgnoreCase("Attack")
                || word_list[0].equalsIgnoreCase("Kill"))
        { fightmethod(word_list); }
        else if (word_list[0].equalsIgnoreCase("See") || word_list[0].equalsIgnoreCase("Examine")
                || word_list[0].equalsIgnoreCase("Look"))
        { lookAt(word_list); }
        else if (word_list[0].equalsIgnoreCase("Take"))
        { take(word_list); }
        else if (word_list[0].equalsIgnoreCase("Drop"))
        { drop(word_list); }
        else if (word_list[0].equalsIgnoreCase("Put")) { putIn(word_list); }
        else if (word_list[0].equalsIgnoreCase("Inventory"))
        { invenChk(); }
        else if (word_list[0].equalsIgnoreCase("Help")) { help(); }
        else if (word_list[0].equalsIgnoreCase("Survey"))
        { survey(); }
        else if(word_list[0].equalsIgnoreCase("Talk") || word_list[0].equalsIgnoreCase("Speak")) { speak(); }
        else if (word_list[0].equalsIgnoreCase("Move") || word_list[0].equalsIgnoreCase("Go")
                || word_list[0].equalsIgnoreCase("Walk") || word_list[0].equalsIgnoreCase("Run"))
        { moving(word_list); }
        else if (word_list[0].equalsIgnoreCase("North") || word_list[0].equalsIgnoreCase("Forward"))
        { north(); }
        else if (word_list[0].equalsIgnoreCase("South") || word_list[0].equalsIgnoreCase("Back"))
        { south(); }
        else if (word_list[0].equalsIgnoreCase("East") || word_list[0].equalsIgnoreCase("Right"))
        { east(); }
        else if (word_list[0].equalsIgnoreCase("West") || word_list[0].equalsIgnoreCase("Left"))
        { west(); }
        else if (word_list[0].equalsIgnoreCase("Up"))
        { up(); }
        else if (word_list[0].equalsIgnoreCase("Down"))
        { down(); }
        else if (word_list[0].equalsIgnoreCase("Quit") || word_list[0].equalsIgnoreCase("End")) {
            so.println("The game will end now.");
            stillPlaying = false;
        }
        else { so.println("That's an unknown command. Please try again."); }
    }

    public void fightmethod(String[] word_list) {
        if(word_list.length == 1 ){
            so.println("What are you going to fight?");
        }
        else if(word_list.length == 2 ) {
            so.printf("And just how are you going to fight %s?\n", word_list[1]);
        }
        else if(!word_list[2].equalsIgnoreCase("with")) {
            so.println("The pen is mighter than the sword, so is grammar when trying to attack.");
        }
        else if (word_list.length == 3 ) {
            so.println("Are you planning on fighting unarmed?");
        }
        else {
            if(!player1.inBag(word_list[3])) {
                so.println("How do you expect to defeat anything with a tool you don't even have!");
            }
            else if(!current.ifThere(word_list[1])) {
                so.println("You're seeing things that don't exist.....");
            }
            else {
                com.company.Inventory equip = player1.findTool(word_list[3]);
                com.company.Obstacle opponent = current.target(word_list[1]);
                if(!opponent.canFight){
                    so.printf("Why are you fighting a %s!\n", opponent.getName());
                }
                else if(!equip.lethal) {
                    so.printf("Your logic is lacking..... %s is clearly not a weapon.\n", equip.getName());
                }
                else {
                    com.company.Enemies battle = current.fighting(word_list[1]);
                    if(!battle.crit(equip.name)) {
                        player1.health = false;
                        so.println("You chose .... poorly.");
                    }
                }
            }
        }
    }

    public void lookAt(String[] word_list) {
        if(word_list.length == 1) {
            so.println("Don't stare at the room like a slack-jawed fool!");
        }
        else {
            for(int i = 0; i < current.interact; i++) {
                if(word_list[1].equalsIgnoreCase(current.inRoom[i].name)) {
                    current.inRoom[i].display();
                    return;
                }
            }
            for(int j = 0; j < player1.possess ; j++){
                if(word_list[1].equalsIgnoreCase(player1.holding[j].name)) {
                    so.printf("The item was in your bag.\n%s\n%s", player1.holding[j].name, player1.holding[j].desc);
                    return;
                }
            }
            so.println("Such an item might exist in your dreams, but not in this world.");
        }
    }

    public void take(String[] word_list) {
        if(word_list.length == 1) {
            so.println("What do you want, the kitchen sink? Be specific!");
        }
        else {
            if(word_list[1].equalsIgnoreCase("All")) {
                int totWeigh = 0;
                for(int j = 0; j < current.itemNum; j++){
                    totWeigh += current.onFloor[j].weight;
                }

                if(totWeigh < player1.strength) {
                    for(int i = 0; i < current.itemNum; i++) {
                        player1.add(current.onFloor[i]);
                    }
                    for(int k = 0; k < player1.possess; k++){
                        current.takeFrom(player1.holding[k].name);
                    }
                }
                else {
                    so.println("All that stuff is too heavy for you to carry!");
                }
            }
            else {
                if(!current.ifThere(word_list[1])) {
                    so.println("You can't take something that doesn't exist!");
                }
                else {
                    if(!current.anItem(word_list[1])) {
                        so.printf("Just because it's there, does not mean you can take the %s!", word_list[1]);
                    }
                    else{
                        player1.add(current.foundIt(word_list[1]));
                        current.takeFrom(word_list[1]);
                    }
                }
            }
        }
    }

    public void drop(String[] word_list) {
        if(word_list.length == 1) {
            so.println("Drop anything but the soap.");
        }
        else {
            if(word_list[1].equalsIgnoreCase("All")) {
                for(int i = 0; i < player1.possess; i++) {
                    current.throwFloor(player1.holding[i]);
                }
                player1.strength = 50;
                player1.possess = 0;
            }
            else {
                if(!player1.inBag(word_list[1])) {
                    so.println("You can't throw away something you never had!");
                }
                else {
                    current.throwFloor(player1.remove(word_list[1]));
                }
            }
        }
    }
    public void putIn(String[] word_list) {
        if( word_list.length == 1 ) {
            so.println("And put what?");
        }
        else if (word_list.length == 2) {
            so.println("And where?");
        }
        else {}
    }
    public void speak() {}

    public void survey() {
        if(current.interact < 1){
            so.println("There is absolutely nothing in here.");
        }
        else {
            for (int i = 0; i < current.interact; i++) {
                current.inRoom[i].display();
            }
        }
    }

    public void invenChk() {
        if(player1.possess < 1){
            so.println("You only have the clothes on your back and nothing else.");
        }
        else {
            for (int i = 0; i < player1.possess; i++) {
                player1.holding[i].display();
            }
        }
    }
    public void help() {}

    public void moving(String[] word_list) {
        if(word_list.length == 1) {
            so.println("Do you even know where you're going?");
        }
        else {
            if (word_list[1].equalsIgnoreCase("North") || word_list[1].equalsIgnoreCase("Forward")) { north(); }
            else if (word_list[1].equalsIgnoreCase("South") || word_list[1].equalsIgnoreCase("Back")) { south(); }
            else if (word_list[1].equalsIgnoreCase("East") || word_list[1].equalsIgnoreCase("Right")) { east(); }
            else if (word_list[1].equalsIgnoreCase("West") || word_list[1].equalsIgnoreCase("Left")) { west(); }
            else if (word_list[1].equalsIgnoreCase("Up")) { up(); }
            else if (word_list[1].equalsIgnoreCase("Down")) { down(); }
            else {
                so.println("Let's face it. You're lost.");
            }
        }
    }

    public void north() {
        if(current.north.isWall()) { current.wall(); }
        else if(current.north.isLock()) { so.println("That's locked!"); }
        else if(!current.north.passable()) { so.println("You can't get through!"); }
        else {
            current = current.north.next;
        }
    }
    public void south() {
        if(current.south.isWall()) { current.wall(); }
        else if(current.south.isLock()) { so.println("That's locked!"); }
        else if(!current.south.passable()) { so.println("You can't get through!"); }
        else {
            current = current.south.next;
        }
    }
    public void east() {
        if(current.east.isWall()){ current.wall(); }
        else if(current.east.isLock()) { so.println("That's locked!"); }
        else if(!current.east.passable()) { so.println("You can't get through!"); }
        else {
            current = current.east.next;
        }
    }
    public void west() {
        if(current.west.isWall()){ current.wall(); }
        else if(current.west.isLock()) { so.println("That's locked!"); }
        else if(!current.west.passable()) { so.println("You can't get through!"); }
        else {
            current = current.west.next;
        }
    }
    public void down() {
        if(current.down.isWall()){ so.println("Unless you're a mole, you can't burrow underground."); }
        else if(current.down.isLock()) { so.println("That's locked!"); }
        else {
            current = current.down.next;
        }
    }
    public void up() {
        if(current.up.isWall()){ so.println("If you somehow managed to grow wings, then maybe you could fly."); }
        else if(current.up.isLock()) { so.println("That's locked!"); }
        else {
            current = current.down.next;
        }
    }

    public void update() {
        currGame.checkWin();
        if(!player1.health || currGame.victory) { stillPlaying = false; }
    }
}

class Player {
    final int carrycap = 50;
    public int strength;
    public boolean health;
    public int score;
    com.company.Inventory[] holding;
    public int possess;

    Player() {
        health = true;
        strength = carrycap;
        holding = new com.company.Inventory[20];
        score = 0;
        possess = 0;
    }

    public void add(com.company.Inventory newItem) {
        if (newItem.weight > strength) {
            so.println("You can't carry this much stuff!");
        }
        else {
            holding[possess] = newItem;
            possess += 1;
            strength = strength - newItem.weight;
            so.printf("The %s is now in your bag.\n", newItem.getName());
        }
    }

    public boolean inBag(String searching) {
        for(int i = 0; i < possess; i++) {
            if(searching.equalsIgnoreCase(holding[i].name)){
                return true;
            }
        }
        return false;
    }

    public com.company.Inventory findTool(String tool) {
        com.company.Inventory temp;
        for(int i = 0; i < possess; i++){
            if(tool.equalsIgnoreCase(holding[i].name)) {
                temp = holding[i];
                return temp;
            }
        }
        return null;
    }

    public com.company.Inventory remove(String item) {
        com.company.Inventory temp;
        for(int i = 0; i < possess; i++){
            if( item.equalsIgnoreCase(holding[i].name)) {
                so.printf("%s was thrown away.", holding[i].getName());
                temp = holding[i];
                strength += holding[i].weight;
                for(int j = i; j < possess; j++) {
                    holding[j] = holding[j+1];
                }
                possess -= 1;
                return temp;
            }
        }
        return null;
    }
}

class Maze{
    com.company.PathFactory factory;
    boolean victory;
    com.company.Room1 room1;
    com.company.Room2 room2;
    com.company.Room3 room3;
    com.company.Room4 room4;
    com.company.Room5 room5;
    com.company.Room6 room6;
    com.company.Room7 room7;
    com.company.Room8 room8;
    com.company.Room9 room9;
    com.company.Room10 room10;
    com.company.Room11 room11;
    com.company.Room12 room12;
    com.company.Room13 room13;
    com.company.Room14 room14;
    com.company.Room15 room15;
    com.company.Room16 room16;
    com.company.Room17 room17;
    com.company.Room18 room18;
    com.company.Room19 room19;
    com.company.Room20 room20;
    com.company.BossRoom bossRoom;

    Maze(com.company.PathFactory factory) {
        this.factory = factory;
        victory = false;
        room1 = new com.company.Room1(factory);
        room2 = new com.company.Room2(factory);
        room3 = new com.company.Room3(factory);
        room4 = new com.company.Room4(factory);
        room5 = new com.company.Room5(factory);
        room6 = new com.company.Room6(factory);
        room7 = new com.company.Room7(factory);
        room8 = new com.company.Room8(factory);
        room9 = new com.company.Room9(factory);
        room10 = new com.company.Room10(factory);
        room11 = new com.company.Room11(factory);
        room12 = new com.company.Room12(factory);
        room13 = new com.company.Room13(factory);
        room14 = new com.company.Room14(factory);
        room15 = new com.company.Room15(factory);
        room16 = new com.company.Room16(factory);
        room17 = new com.company.Room17(factory);
        room18 = new com.company.Room18(factory);
        room19 = new com.company.Room19(factory);
        room20 = new com.company.Room20(factory);
        bossRoom = new com.company.BossRoom(factory);
    }

    public void createPath() {
        room1.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room2.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room3.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room4.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room5.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room6.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room7.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room8.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room9.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room10.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room11.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room12.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room13.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room14.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room15.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room16.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room17.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room18.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room19.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        room20.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
        bossRoom.pathway(room1, room2, room3, room4, room5, room6, room7, room8, room9, room10,
                room11, room12, room13, room14, room15, room16, room17, room18, room19, room20, bossRoom);
    }

    public void checkWin() {
        if(!bossRoom.qanon.alive) { victory = true; }
    }

}

class Side {
    boolean traverse;
    boolean wall;
    boolean closed;
    com.company.Room next;

    Side(){ wall = true; }

    public boolean isWall() { return wall; }
    public boolean isLock() { return closed; }
    public boolean passable() { return traverse; }
}

class Door extends com.company.Side {
    Door(com.company.Room next) {
        this.next = next;
        wall = false;
        closed = false;
        traverse = true;
    }
}

class Locked extends com.company.Door {
    Locked(com.company.Room next) {
        super(next);
        closed = true;
        traverse = false;
    }

    public void condition() {
        closed = false;
        traverse = true;
    }
}

class PathFactory {
    public com.company.Side createWall() { return new com.company.Side(); }
    public com.company.Door createDoor(com.company.Room room) { return new com.company.Door(room); }
    public com.company.Locked createLock(com.company.Room room) { return new com.company.Locked(room); }

    public com.company.Kiise spawnKiise() { return new com.company.Kiise(); }
    public com.company.Octostone spawnOcto() { return new com.company.Octostone(); }
    public com.company.Qanon spawnQanon() { return new com.company.Qanon(); }

    public com.company.SilverKey silverKey() { return new com.company.SilverKey(); }
    public com.company.DungeonKey dungeonKey() { return new com.company.DungeonKey(); }
    public com.company.QuadFire quadFire() { return new com.company.QuadFire(); }
    public com.company.QuadEarth quadEarth() { return new com.company.QuadEarth(); }
    public com.company.QuadAir quadAir() { return new com.company.QuadAir(); }
    public com.company.QuadWater quadWater() { return new com.company.QuadWater(); }

    public com.company.WoodBlade woodBlade() { return new com.company.WoodBlade(); }
    public com.company.MisterSword misterSword() { return new com.company.MisterSword(); }
    public com.company.FireRod fireRod() { return new com.company.FireRod(); }
    public com.company.Bombs bombs() { return new com.company.Bombs(); }
    public com.company.WoodShield woodShield() { return new com.company.WoodShield(); }
    public com.company.HighShield highShield() { return new com.company.HighShield(); }

    public com.company.Hermit spawnHermit() { return new com.company.Hermit(); }
    public com.company.Painting spwnPaint() { return new com.company.Painting(); }
    public com.company.Boulder spwnBoulder() { return new com.company.Boulder(); }
    public com.company.Case spnCase() { return new com.company.Case(); }
}

// ---------------------------------------------------------------------------------------------------------------------------

abstract class Obstacle {
    public String name;
    public String desc;
    boolean canFight;

    public void display() {
        so.printf("%s - %s\n", getName(), desc);
    }

    public String getName() { return name; }
    public String toString() { return getName(); }
}

class Container extends com.company.Obstacle {
    com.company.Inventory[] holds;

    Container() {
        holds = new com.company.Inventory[5];
    }
}

class Inventory extends com.company.Obstacle {
    public int weight;
    boolean lethal;

    Inventory() {
        weight = 0;
        lethal = false;
        canFight = false;
    }
}

class Weaponry extends com.company.Inventory {
    Weaponry() {
        lethal = true;
    }
}

abstract class Enemies extends com.company.Obstacle {
    public boolean alive;
    public com.company.Weaponry weakness;

    Enemies() {
        canFight = true;
        alive = true;
    }

    public void killed() { alive = false; }

    abstract boolean crit(String weapon);
}

abstract class Room {
    public String name;
    public String descrip;

    public com.company.Side north;
    public com.company.Side south;
    public com.company.Side east;
    public com.company.Side west;
    public com.company.Side up;
    public com.company.Side down;

    com.company.Inventory[] onFloor = {};
    public int itemNum;
    com.company.Obstacle[] inRoom = {};
    public int interact;
    com.company.PathFactory direction;

    Room(com.company.PathFactory direction) {
        this.direction = direction;
        onFloor = new com.company.Inventory[25];
        itemNum = 0;
        inRoom = new com.company.Obstacle[30];
        interact = 0;
        north = direction.createWall();
        south = direction.createWall();
        east = direction.createWall();
        west = direction.createWall();
        up = direction.createWall();
        down = direction.createWall();
    }

    public String getName() {return name;}
    public String getDescrip() {return descrip;}
    public String toString() {
        String temp = onFloor[0].getName();

        if(itemNum == 2){
            temp = temp.concat(" and ").concat(onFloor[1].getName());
        }
        else if(itemNum >= 3) {
            for(int i = 1; i < itemNum - 1; i++) {
                temp = temp.concat(", ");
                temp = temp.concat(onFloor[i].getName());
            }
            temp.concat(", and ").concat(onFloor[itemNum-1].getName());
        }

        return temp;
    }

    public void optional() {
        if(itemNum == 1) {
            so.printf("A %s is just laying there on the ground.\n", this);
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.\n", this);
        }
    }

    public void addBckgrnd(com.company.Obstacle anoth) {
        inRoom[interact] = anoth;
        interact += 1;
    }

    public void throwFloor(com.company.Inventory litter) {
        onFloor[itemNum] = litter;
        itemNum += 1;
        addBckgrnd(litter);
    }

    public boolean ifThere(String search){
        for(int i = 0; i < interact; i++) {
            if(search.equalsIgnoreCase(inRoom[i].name)) {
                return true;
            }
        }
        return false;
    }

    public boolean anItem(String search) {
        for(int i = 0; i < itemNum; i++) {
            if(search.equalsIgnoreCase(onFloor[i].name)) {
                return true;
            }
        }
        return false;
    }

    public com.company.Inventory foundIt(String finding) {
        for(int i = 0; i < itemNum; i++) {
            if(finding.equalsIgnoreCase(onFloor[i].name)) {
                return onFloor[i];
            }
        }
        return null;
    }

    public void takeFrom(String remove) {
        for(int i = 0; i < interact; i++){
            if(remove.equalsIgnoreCase(inRoom[i].name)){
                for(int j = i; j < interact; j++) {
                    inRoom[j] = inRoom[j+1];
                }
                interact -= 1;
                break;
            }
        }
        for (int k = 0; k < itemNum; k++) {
            if(remove.equalsIgnoreCase(onFloor[k].name)) {
                for(int l = k; l < itemNum; l++) {
                    onFloor[l] = onFloor[l+1];
                }
                itemNum -= 1;
                break;
            }
        }
    }

    public com.company.Obstacle target(String search) {
        com.company.Obstacle plcehldr;
        for(int i = 0; i < interact; i++) {
            if(search.equalsIgnoreCase(inRoom[i].name)) {
                plcehldr = inRoom[i];
                return plcehldr;
            }
        }
        return null;
    }

    public com.company.Enemies fighting(String search) {
        com.company.Enemies plcehldr;
        for(int i = 0; i < interact; i++) {
            if(search.equalsIgnoreCase(inRoom[i].name)) {
                plcehldr = (com.company.Enemies)inRoom[i];
                return plcehldr;
            }
        }
        return null;
    }

    abstract void wall();
    abstract void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                          com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                          com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss);
}

// -----------------------------------------------------------------------------------------------------------------

class SilverKey extends com.company.Inventory {
    SilverKey() {
        name = "Key";
        desc = "A key made of pure silver. It should open a door somewhere.";
    }
}

class DungeonKey extends com.company.Inventory {
    DungeonKey() {
        name = "Dungeon";
        desc = "A rather ominous looking key. It probably fits a special looking door.";
    }
    public String getName() { return "Dungeon Key"; }
}

class QuadFire extends com.company.Inventory {
    QuadFire(){
        name = "QuadFire";
        desc = "A fragment of the QuadForce; this one is embued with the power of red flames.";
    }
}

class QuadEarth extends com.company.Inventory {
    QuadEarth() {
        name = "QuadEarth";
        desc = "A fragment of the QuadForce; this one is embued with the power of yellow dirt.";
    }
}

class QuadWater extends com.company.Inventory {
    QuadWater() {
        name = "QuadWater";
        desc = "A fragment of the QuadForce; this one is embued with the power of blue liquid.";
    }
}

class QuadAir extends com.company.Inventory {
    QuadAir() {
        name = "QuadAir";
        desc = "A fragment of the QuadForce; this one is embued with the power of green wind.";
    }
}

// --------------------------------------------------------------------------------------------------------------------------------

class WoodBlade extends com.company.Weaponry {
    WoodBlade() {
        name = "Wooden";
        desc = "A simple lightweight weapon.";
        weight = 10;
    }

    public String getName() { return "Wooden Blade"; }
}

class MisterSword extends com.company.Weaponry {
    MisterSword() {
        name = "Mister";
        desc = "This Sword shows your status as the Chosen Hero.";
        weight = 25;
    }

    public String getName() { return "Mister Sword"; }
}

class FireRod extends com.company.Weaponry {
    FireRod() {
        name = "Flame";
        desc = "Allows you to summon a flame to light your way.";
        weight = 15;
    }

    public String getName() { return "Flame Rod"; }
}

class Bombs extends com.company.Weaponry {
    Bombs() {
        name = "Bombs";
        desc = "These explosive bombs can really pack a punch.";
        weight = 20;
    }
}

class WoodShield extends com.company.Weaponry {
    WoodShield () {
        name = "Buckler";
        desc = "A small Shield made of Wood.";
        weight = 15;
    }
}

class HighShield extends com.company.Weaponry {
    HighShield() {
        name = "Highlian";
        desc = "An indestructible Shield that can only be wielded by the Swordmaster of Light.";
        weight = 25;
    }

    public String getName() { return "Highlian Shield"; }
}

class QuadForce extends com.company.Weaponry {
    QuadForce() {
        name = "QuadForce";
        desc = "The ultimate embodiment of the Forces of Light.";
        weight = 0;
    }
}

// ------------------------------------------------------------------------------------------------

class Hermit extends com.company.Obstacle {
    Hermit() {
        name = "Hermit";
        desc = "An old man who smells like he hasn't bathed in a decade.";
    }
}

class Boulder extends com.company.Enemies {
    Boulder() {
        weakness = new com.company.Bombs();
        name = "Boulder";
        desc = "A gigantic piece of rubble. There may be something underneath.";
        alive = true;
    }

    public boolean crit(String weapon) {
        if(weakness.name.equals(weapon)) {
            killed();
            so.println("The giant boulder was reduced to dust!");
            return true;
        }
        else {
            so.println("The heavy rock refuses to budge!");
            return false;
        }
    }
}

class Painting extends com.company.Container {
    Painting() {
        name = "Painting";
        desc = "It appears to be some sort of abstract portrait.";
        holds = new com.company.Inventory[1];
    }
}

class Case extends com.company.Container {
    Case() {
        name = "Case";
        desc = "A decorative glass Case for decorational purposes.";
        holds = new com.company.Inventory[3];
    }
}

// ---------------------------------------------------------------------------------------------------------------------

class Kiise extends com.company.Enemies {
    Kiise() {
        super();
        weakness = new com.company.WoodBlade();
        name = "Kiise";
        desc = "They're rather quick creatures, try swatting them with a melee weapon.";
    }

    public boolean crit(String weapon) {
        if(weakness.name.equals(weapon)) {
            killed();
            so.println("The Kiise dies from a direct bludgeon to the head.");
            return true;
        }
        else {
            so.println("The Kiise swoops in for the killing blow.");
            return false;
        }
    }
}

class Octostone extends com.company.Enemies {
    Octostone() {
        super();
        weakness = new com.company.WoodShield();
        name = "Octostone";
        desc = "It is weak to its own attack, utilise that information.";
    }

    public boolean crit(String weapon) {
        if (weakness.name.equals(weapon)) {
            killed();
            so.println("You executed a perfect deflect of its own attack!\nIt disintegrates into a pile of black dust.");
            return true;
        }
        else {
            so.println("The Octostone shoots a fatal projectile, striking you in the head.");
            return false;
        }
    }
}

class Qanon extends com.company.Enemies {
    boolean firstForm;
    boolean secForm;
    boolean finalForm;

    Qanon() {
        super();
        weakness = new com.company.MisterSword();
        finalForm = true;
        secForm = true;
        firstForm = true;
        name = "Qanon";
        desc = "Your one true Enemy";
    }

    public boolean crit(String weapon) {
        if(weakness.name.equals(weapon)) {
            if(firstForm) {
                firstForm = false;
                so.println("The ghastly king falls to his knees.");
            }
            else if (secForm) {
                secForm = false;
                so.println("The pig-like monstrosity lets out its final squeal!");
            }
            else {
                killed();
                so.println("The dark miasma finally dissipates.");
            }
            return true;
        }
        else {
            so.println("Qanon unleashes his ultimate magick!");
            return false;
        }
    }
}

// ------------------------------------------------------------------------------------------------------------------------

class Room1 extends com.company.Room {
    com.company.Painting painting;

    Room1(com.company.PathFactory direction) {
        super(direction);
        name = "The West Wing";
        descrip = "The room is ornately decorated. A large Painting hangs on the wall.\n" + "The only exit is to the North.";
        painting = direction.spwnPaint();
        addBckgrnd(painting);
    }

    public void wall() { so.println("There is no way through the thick stone wall."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room2);
    }
}

class Room2 extends com.company.Room {
    Room2(com.company.PathFactory direction) {
        super(direction);
        name = "Armoury";
        descrip = "A long abandoned weaponry storage room. The weapons are rusted from disuse.\n" + "There are doors leading North, South, and East.";
        throwFloor(direction.woodShield());
    }

    public void wall() { so.println("It's impossible to phase through the castle walls."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room3);
        south = direction.createDoor(room1);
        east = direction.createDoor(room7);
    }
}

class Room3 extends com.company.Room {
    Room3(com.company.PathFactory direction) {
        super(direction);
        name = "Narrow Path";
        descrip = "You’re in a tight passageway that goes North and South. It is a rather tight squeeze.";
    }

    public void wall() { so.println("There isn't even enough room to turn in any direction."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room6);
        south = direction.createDoor(room2);
    }
}

class Room4 extends com.company.Room {
    Room4(com.company.PathFactory direction) {
        super(direction);
        name = "Balcony";
        descrip = "You’re standing on a high balcony where falling off the edges ensures certain death. There is a constant breeze at all times.\n" + "The only door leads East.";
    }

    public void wall() { so.println("You will fall to your death off this balcony!"); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        east = direction.createDoor(room5);
    }
}

class Room5 extends com.company.Room {
    Room5(com.company.PathFactory direction) {
        super(direction);
        name = "Corridor";
        descrip = "You’re in a corridor that goes East and West.";
        throwFloor(direction.bombs());
    }

    public void wall() { so.println("You're not a ghost; you can't phase through walls."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        east = direction.createDoor(room11);
        west = direction.createDoor(room4);
    }
}


class Room6 extends com.company.Room {
    Room6(com.company.PathFactory direction) {
        super(direction);
        name = "Hallway";
        descrip = "There are paths open in every direction: North, East, South, West.";
    }

    public void wall() { so.println("You can't go this way."); }

    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room3);
        south = direction.createDoor(room7);
        east = direction.createDoor(room20);
        west = direction.createDoor(room7);
    }
}

class Room7 extends com.company.Room {
    Room7(com.company.PathFactory direction) {
        super(direction);
        name = "Fountain Room";
        descrip = "A water Fountain sits at the centre of the room.\n"+ "The room opens up in the East, West, and North directions.";
    }

    public void wall() { so.println("That's a steep waterfall!"); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room6);
        east = direction.createDoor(room6);
        west = direction.createDoor(room2);
    }
}

class Room8 extends com.company.Room {
    com.company.Boulder boulder;

    Room8(com.company.PathFactory direction) {
        super(direction);
        name = "Store Room";
        descrip = "Back when there were still people, this would have been where they stored food and ingredients.\n"
                + "Now it is in a state of disarray with debris everywhere.\n"
                + "The entrance and exit are North and South.";
        boulder = direction.spwnBoulder();
        addBckgrnd(boulder);
    }

    public void optional() {
        if(boulder.alive) {
            so.println("A massive boulder just block the middle of the room.");
        }

        if(itemNum == 1) {
            so.printf("A %s is just laying there on the ground.\n", toString());
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.\n", toString());
        }
    }

    public void wall() { so.println("The boulders will collapse and crush you."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room10);
        south = direction.createDoor(room9);
    }
}

class Room9 extends com.company.Room {
    Room9(com.company.PathFactory direction) {
        super(direction);
        name = "Kitchen";
        descrip = "The stoves have long since been abandoned.\n" + "The exits are North and West.";
        throwFloor(direction.silverKey());
    }

    public void wall() { so.println("You can't go through the greasy walls."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room10);
        west = direction.createDoor(room8);
    }
}

class Room10 extends com.company.Room {
    Room10(com.company.PathFactory direction) {
        super(direction);
        name = "Hallway";
        descrip = "There are paths open in every direction: North, East, South, West.";
    }

    public void wall() { so.println("You can't go this way."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room20);
        south = direction.createDoor(room9);
        east = direction.createDoor(room18);
        west = direction.createDoor(room8);
    }
}

class Room11 extends com.company.Room {
    com.company.Kiise kiise;

    Room11(com.company.PathFactory direction) {
        super(direction);
        name = "Dining Room";
        descrip = "The giant table would have truly held a feast for a king.\n" + "You can see paths in each direction: North, East, South, West.";
        kiise = direction.spawnKiise();
        addBckgrnd(kiise);
    }

    public void optional() {
        if(kiise.alive) {
            so.println("But the bat-like Kiise blocks your way North and West.");
            north.traverse = false;
            west.traverse = false;
        }
        else {
            north.traverse = true;
            west.traverse = true;
        }
    }

    public void wall() {so.println("You can't go this way.");}
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room12);
        south = direction.createDoor(room20);
        east = direction.createDoor(room15);
        west = direction.createDoor(room5);
    }
}

class Room12 extends com.company.Room {
    Room12(com.company.PathFactory direction) {
        super(direction);
        name = "Room of the Hero";
        descrip = "Something about the room just calls out to you.\n" +
                "You can only proceed East or South.";
    }

    public void wall() { so.println("You can't go through the thick walls."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        south = direction.createDoor(room11);
        east = direction.createDoor(room13);
    }
}

class Room13 extends com.company.Room {
    Room13(com.company.PathFactory direction) {
        super(direction);
        name = "Shrine of the Goddess";
        descrip = "You’re standing in what looks like a shrine. You can feel an ethereal power emanating from every corner of the room.\n" +
                "The only way out is West.";
    }

    public void wall() { so.println("A dense forest blocks your way."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        west = direction.createDoor(room12);
    }
}

class Room14 extends com.company.Room {
    int entry;
    Room14(com.company.PathFactory direction) {
        super(direction);
        name = "Smithy";
        descrip = "The forge has long since gone cold." + "The heavy doors open up towards the South and the West.";
        throwFloor(direction.highShield());
        entry = 0;
    }

    public void optional(){
        if(entry == 0){
            so.println("The Highlian Shield hangs precariously on the wall. It could fall at any moment.");
        }

        if(itemNum == 1 && entry > 0) {
            so.printf("A %s is just laying there on the ground.\n", this);
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.\n", this);
        }

        entry += 1;
    }

    public void wall() { so.println("The iron building bars your way."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        south = direction.createDoor(room15);
        west = direction.createDoor(room15);
    }
}

class Room15 extends com.company.Room {
    com.company.Octostone octo;

    Room15(com.company.PathFactory direction) {
        super(direction);
        name = "Training Grounds";
        descrip = "A dirt arena where soldiers once tested their mettle.\n" + "There are paths leading North, East, South, and West.";
        octo = direction.spawnOcto();
        addBckgrnd(octo);
    }

    public void optional() {
        if(octo.alive){
            so.println("Be wary! An Octostone firmly roots itself to the North and East, shooting anyone that dares approach it.");
            north.traverse = false;
            east.traverse = false;
        }
        else {
            north.traverse = true;
            east.traverse = true;
        }
        if(itemNum == 1) {
            so.printf("A %s is just laying there on the ground.\n", toString());
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.", toString());
        }
    }

    public void wall() {so.println("You can't go this way.");}
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room14);
        south = direction.createDoor(room17);
        east = direction.createDoor(room14);
        west = direction.createDoor(room11);
    }
}

class Room16 extends com.company.Room {
    Room16(com.company.PathFactory direction) {
        super(direction);
        name = "Hearth";
        descrip = "A golden Brazier warms the entire room.\n" + "The only entrance is West.";
    }

    public void wall() { so.println("You can't touch the flaming hot stones."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        west = direction.createLock(room17);
    }
}

class Room17 extends com.company.Room {
    com.company.Case glssCase;
    Room17(com.company.PathFactory direction) {
        super(direction);
        name = "Trophy Room";
        descrip = "Priceless relics line the room. Some rest in a glass Case.\n" + "There are doors in every direction: North, East, South, West.";
        glssCase = direction.spnCase();
        addBckgrnd(glssCase);
        glssCase.holds[0] = direction.fireRod();
    }

    public void optional() {
        if(east.closed){
            so.println("It appears that the East side door remains firmly locked.");
        }
        if(itemNum == 1) {
            so.printf("A %s is just laying there on the ground.\n", toString());
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.", toString());
        }
    }

    public void wall() {so.println("You can't go this way.");}
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room15);
        south = direction.createDoor(room18);
        east = direction.createLock(room16);
        west = direction.createDoor(room20);
    }
}

class Room18 extends com.company.Room {
    com.company.Hermit hermit;

    Room18(com.company.PathFactory direction) {
        super(direction);
        name = "Underground Passage";
        descrip = "At the centre of the room, a Hermit in robes just stands there, menacingly.\n" + "The cave opens up to the North, East, and West.";
        hermit = direction.spawnHermit();
        addBckgrnd(hermit);
    }

    public void wall() { so.println("The cave blocks your way."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room17);
        east = direction.createDoor(room19);
        west = direction.createDoor(room10);
    }
}

class Room19 extends com.company.Room {
    Room19(com.company.PathFactory direction) {
        super(direction);
        name = "Dead End Cave";
        descrip = "You’re standing in a dark cave with walls blocking every direction. The only way out is to the North.";
    }

    public void wall() { so.println("The dark cave completely surrounds you."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room18);
    }
}

class Room20 extends com.company.Room {
    Room20(com.company.PathFactory direction) {
        super(direction);
        name = "Dungeon Entrance";
        descrip = "There are doors open in every direction: North, East, South, West.\n"
                +  "However, there is a giant Gate in the floor with an ornate pattern at the centre.";
    }

    public void optional() {
        if(down.closed) {
            so.println("That suspicious Gate is locked, only a special key can open it.");
        }
        if(itemNum == 1) {
            so.printf("A %s is just laying there on the ground.\n", toString());
        }
        else if (itemNum > 1){
            so.printf("%s litter the floor.", toString());
        }
    }

    public void wall() { so.println("You can't go this way."); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        north = direction.createDoor(room11);
        south = direction.createDoor(room10);
        east = direction.createDoor(room17);
        west = direction.createDoor(room6);
        down = direction.createLock(boss);
    }
}

class BossRoom extends com.company.Room {
    com.company.Qanon qanon;

    BossRoom(com.company.PathFactory direction) {
        super(direction);
        name = "Boss Lair";
        descrip = "Here lies the throne room of Qanon: Bringer of Calamity.\n" + "The only escape is Up through the ceiling.";
        qanon = direction.spawnQanon();
        addBckgrnd(qanon);
    }

    public void optional() {
        if(qanon.firstForm) {
            so.println("Qanon waits in front of you, an apparition of what was once a human king.");
        }
        else if(qanon.secForm) {
            qanon.weakness = new com.company.HighShield();
            so.println("Qanon takes on a new form, one resembling that of a monstrous boar.");
        }
        else if(qanon.finalForm) {
            qanon.weakness = new com.company.QuadForce();
            so.println("This is Qanon's true form: a nightmare creature dripping darkness.");
        }
    }

    public void wall() { so.println("YOU SHALL NOT PASS!!!"); }
    public void pathway(com.company.Room1 room1, com.company.Room2 room2, com.company.Room3 room3, com.company.Room4 room4, com.company.Room5 room5, com.company.Room6 room6, com.company.Room7 room7,
                        com.company.Room8 room8, com.company.Room9 room9, com.company.Room10 room10, com.company.Room11 room11, com.company.Room12 room12, com.company.Room13 room13, com.company.Room14 room14,
                        com.company.Room15 room15, com.company.Room16 room16, com.company.Room17 room17, com.company.Room18 room18, com.company.Room19 room19, com.company.Room20 room20, com.company.BossRoom boss) {
        up = direction.createLock(room20);
    }
}