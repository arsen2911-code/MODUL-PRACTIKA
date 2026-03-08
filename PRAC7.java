import java.util.*;

interface ICommand{
    void execute();
    void undo();
}

class Light{
    void on(){System.out.println("Light ON");}
    void off(){System.out.println("Light OFF");}
}

class AirConditioner{
    void on(){System.out.println("AC ON");}
    void off(){System.out.println("AC OFF");}
}

class TV{
    void on(){System.out.println("TV ON");}
    void off(){System.out.println("TV OFF");}
}

class LightOnCommand implements ICommand{
    Light light;
    LightOnCommand(Light l){light=l;}
    public void execute(){light.on();}
    public void undo(){light.off();}
}

class LightOffCommand implements ICommand{
    Light light;
    LightOffCommand(Light l){light=l;}
    public void execute(){light.off();}
    public void undo(){light.on();}
}

class ACOnCommand implements ICommand{
    AirConditioner ac;
    ACOnCommand(AirConditioner ac){this.ac=ac;}
    public void execute(){ac.on();}
    public void undo(){ac.off();}
}

class TVOnCommand implements ICommand{
    TV tv;
    TVOnCommand(TV tv){this.tv=tv;}
    public void execute(){tv.on();}
    public void undo(){tv.off();}
}

class MacroCommand implements ICommand{
    List<ICommand> commands;
    MacroCommand(List<ICommand> c){commands=c;}
    public void execute(){
        for(ICommand c:commands)c.execute();
    }
    public void undo(){
        for(ICommand c:commands)c.undo();
    }
}

class RemoteControl{

    Map<Integer,ICommand> slots=new HashMap<>();
    Stack<ICommand> undoStack=new Stack<>();
    Stack<ICommand> redoStack=new Stack<>();

    void setCommand(int slot,ICommand cmd){
        slots.put(slot,cmd);
    }

    void pressButton(int slot){
        ICommand cmd=slots.get(slot);
        if(cmd==null){
            System.out.println("No command");
            return;
        }
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    void undo(){
        if(undoStack.isEmpty()){
            System.out.println("Nothing to undo");
            return;
        }
        ICommand cmd=undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
    }

    void redo(){
        if(redoStack.isEmpty()){
            System.out.println("Nothing to redo");
            return;
        }
        ICommand cmd=redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
    }
}

abstract class ReportGenerator{

    public final void generateReport(){
        fetchData();
        formatData();
        generateHeader();
        saveReport();
        if(customerWantsSend())
            sendEmail();
    }

    void fetchData(){
        System.out.println("Fetching data");
    }

    abstract void formatData();
    abstract void generateHeader();
    abstract void saveReport();

    boolean customerWantsSend(){
        return false;
    }

    void sendEmail(){
        System.out.println("Sending email");
    }
}

class PdfReport extends ReportGenerator{
    void formatData(){System.out.println("PDF format");}
    void generateHeader(){System.out.println("PDF header");}
    void saveReport(){System.out.println("PDF saved");}
}

class ExcelReport extends ReportGenerator{
    void formatData(){System.out.println("Excel format");}
    void generateHeader(){System.out.println("Excel header");}
    void saveReport(){System.out.println("Excel saved");}
    boolean customerWantsSend(){return true;}
}

class HtmlReport extends ReportGenerator{
    void formatData(){System.out.println("HTML format");}
    void generateHeader(){System.out.println("HTML header");}
    void saveReport(){System.out.println("HTML saved");}
}

interface IMediator{
    void sendMessage(String msg,IUser sender,String channel);
    void addUser(IUser user,String channel);
    void removeUser(IUser user,String channel);
}

interface IUser{
    void send(String msg,String channel);
    void receive(String msg);
    String getName();
}

class ChannelMediator implements IMediator{

    Map<String,List<IUser>> channels=new HashMap<>();

    public void addUser(IUser user,String channel){
        channels.putIfAbsent(channel,new ArrayList<>());
        channels.get(channel).add(user);
        notifyUsers(channel,user.getName()+" joined");
    }

    public void removeUser(IUser user,String channel){
        if(!channels.containsKey(channel))return;
        channels.get(channel).remove(user);
        notifyUsers(channel,user.getName()+" left");
    }

    public void sendMessage(String msg,IUser sender,String channel){
        if(!channels.containsKey(channel)){
            System.out.println("Channel not found");
            return;
        }
        for(IUser u:channels.get(channel)){
            if(u!=sender)
                u.receive(sender.getName()+": "+msg);
        }
    }

    void notifyUsers(String channel,String msg){
        for(IUser u:channels.get(channel))
            u.receive("[SYSTEM] "+msg);
    }
}

class User implements IUser{

    String name;
    IMediator mediator;

    User(String name,IMediator m){
        this.name=name;
        mediator=m;
    }

    public String getName(){return name;}

    public void send(String msg,String channel){
        mediator.sendMessage(msg,this,channel);
    }

    public void receive(String msg){
        System.out.println(name+" received -> "+msg);
    }
}

public class PRAC7 {

    public static void main(String[] args){

        Light light=new Light();
        AirConditioner ac=new AirConditioner();
        TV tv=new TV();

        RemoteControl remote=new RemoteControl();

        remote.setCommand(1,new LightOnCommand(light));
        remote.setCommand(2,new ACOnCommand(ac));
        remote.setCommand(3,new TVOnCommand(tv));

        remote.pressButton(1);
        remote.pressButton(2);
        remote.undo();
        remote.redo();

        List<ICommand> macroList=new ArrayList<>();
        macroList.add(new LightOnCommand(light));
        macroList.add(new ACOnCommand(ac));
        macroList.add(new TVOnCommand(tv));

        MacroCommand macro=new MacroCommand(macroList);

        remote.setCommand(4,macro);
        remote.pressButton(4);

        ReportGenerator pdf=new PdfReport();
        pdf.generateReport();

        ReportGenerator excel=new ExcelReport();
        excel.generateReport();

        ReportGenerator html=new HtmlReport();
        html.generateReport();

        IMediator mediator=new ChannelMediator();

        User u1=new User("Alice",mediator);
        User u2=new User("Bob",mediator);
        User u3=new User("Charlie",mediator);

        mediator.addUser(u1,"general");
        mediator.addUser(u2,"general");
        mediator.addUser(u3,"general");

        u1.send("Hello everyone","general");
        u2.send("Hi","general");

        mediator.removeUser(u3,"general");
    }
}