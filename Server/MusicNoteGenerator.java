package Server;

import java.io.Serializable;

import compute.Task;

/*
    OBSERVAÇÃO
    -------------------
    C  = DÓ
    C# = DÓ SUSTENIDO
    D  = RÉ
    E = MI
    F = FÁ
    F# = FÁ SUSTENIDO
    G = SOL
    G# = SOL SUSTENIDO
    A = LÁ
    A# = LÁ SUSTENIDO
    B = SI
    -------------------
 */


public class MusicNoteGenerator implements Task<String[]>, Serializable {
    private String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private int[] majorPattern = {2, 2, 1, 2, 2, 2, 1};
    private int[] minorPattern = {2, 1, 2, 2, 1, 2, 2};
    private String key;
    private String mode;
    private int index;
    private int resultIndex;
    private String[] result;

    MusicNoteGenerator(String key, String mode) {
        this.key = key;
        this.mode = mode;
        this.result = new String[8];
        this.index = -1;
        this.resultIndex = 0;
    }
    //inicializa as variáveis
    public void start(String start) {
        for(int i = 0; i < notes.length; i++) {
            if(start.equals(notes[i])) {
                index = i;
            }
        }

        if(index == -1) {
            System.out.println("key not valid");
        }

        result[resultIndex] = notes[index];
        resultIndex++;
    }

    public String[] execute() {
        return result();
    }

    //itera o algoritmo que "caminha" por entre as notas existentes em String[] notes
    public void iterate(int step) {
        int newindex = index + step;

        if(newindex > (notes.length - 1)) {
            newindex = -1 + step;
        }

        index = newindex;

        result[resultIndex] = notes[index];
        resultIndex++;
        return;
    }
    //gera uma escala maior natural
    public void generateMajor() {
        for(int i = 0; i < majorPattern.length; i++) {
            iterate(majorPattern[i]);
        }
        return;
    }
    //gera uma escala menor natural
    public void generateMinor() {
        for(int i = 0; i < minorPattern.length; i++) {
            iterate(minorPattern[i]);
        }
        return;
    }
    //mostra o resultado
    public String[] result() {
        System.out.println("key: "+key);
        start(key);
        
        if(mode.equals("major")) {
            generateMajor();
        }

        else if(mode.equals("minor")) {
            generateMinor();
        }

        return this.result;
    }
    
    // PARA FIM DE TESTES
    // public static void main(String args[]) {
    //     String[] result = new MusicNoteGenerator("B", "minor").result();

    //     for(int i = 0; i < result.length; i++){
    //         System.out.println("note: " + result[i]);
    //     }

    // }

}