package Server;

import java.io.Serializable;

import compute.Task;

public class StringCryptographer implements Task<String>, Serializable {

    public String message;
    public String passphrase;
    public final char[] dictionary = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' ', ',', '!', '?'};
    public int index;
    public int messageIndex;
    public String action;

    public String execute() {

        StringCryptographer sc = new StringCryptographer(this.message, this.passphrase, this.action);

        if(action.equals("encrypt")) {
            return sc.encrypt();
        }

        else if(action.equals("decrypt")) {
            return sc.decrypt();
        }


        return sc.encrypt(); 
    }
    //construtor
    StringCryptographer(String message, String passphrase, String action) {
        this.message = message.toUpperCase();
        this.passphrase = passphrase.toUpperCase();
        this.index = 0;
        this.messageIndex = 0;
        this.action = action;
    }
    // pega a soma dos chars de uma string
    public static int getCharValueSum(String input) {
        int sum = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            sum += (int) c;
        }
        return sum;
    }

    // retorna o index de um dado char no dicionário
    public int findInDictionary(char c) {
        for(int i = 0; i < dictionary.length; i++) {
            if(dictionary[i] == c) {
                return i;
            }
        }
        System.out.println("char not in dictionary: " + c);
        return -1;
    }


    //roda uma iteração de encriptação de um caractere
    public void iterateEncrypt(int step) {
        
        char[] messageChar = this.message.toCharArray();
        
        int dictionaryIndex = findInDictionary(messageChar[messageIndex]);

        // System.out.println("dictionaryIndex: "+dictionaryIndex);

        int offset = step % (dictionary.length - 1);
        int difference = 0;
        int newindex = offset + dictionaryIndex;

        if(newindex > dictionary.length - 1) {
            difference = newindex - (dictionary.length - 1);
            newindex = -1 + difference;
        }

        if(newindex < 0) {
            newindex = offset;
        }

        System.out.println(dictionary[dictionaryIndex]+" -> "+dictionary[newindex]);

        messageChar[messageIndex] = dictionary[newindex];
        this.message = new String(messageChar);
        messageIndex++;
        return;
    }
    //roda uma iteração de decriptação de um caractere
    public void iterateDecrypt(int step) {
        
        char[] messageChar = this.message.toCharArray();
        
        int dictionaryIndex = findInDictionary(messageChar[messageIndex]);


        int offset = step % (dictionary.length - 1);
        int difference = 0;
        int newindex = dictionaryIndex - offset;

        if(newindex < 0) {
            difference = (-newindex) - 1;
            newindex = (dictionary.length -1) - difference;
        }


        System.out.println(dictionary[dictionaryIndex]+" -> "+dictionary[newindex]);

        messageChar[messageIndex] = dictionary[newindex];
        this.message = new String(messageChar);
        messageIndex++;
        return;
    }

    public String encrypt() {
        this.messageIndex = 0;
        int sum = getCharValueSum(this.passphrase);
        System.out.println("dictionary length: " + this.dictionary.length);
        for(int i = 0; i < this.message.length(); i++){
            iterateEncrypt(sum);
        }

        return this.message;
    }

    public String decrypt() {
        this.messageIndex = 0;
        int sum = getCharValueSum(this.passphrase);
        System.out.println("dictionary length: " + this.dictionary.length);
        for(int i = 0; i < this.message.length(); i++){
            iterateDecrypt(sum);
        }

        return this.message;
    }

    // PARA FINS DE TESTE
    // public static void main(String[] args) {
    //     String A = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ,!?";
    //     String B = "secret key";

    //     StringCryptographer sc = new StringCryptographer(A, B);

    //     String encrypted = sc.encrypt();
    //     System.out.println("Encrypted: " + encrypted);

    //     String decrypted = sc.decrypt();
    //     System.out.println("Decrypted: "+decrypted);
        
    // }
}
