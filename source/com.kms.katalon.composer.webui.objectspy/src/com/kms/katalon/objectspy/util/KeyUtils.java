package com.kms.katalon.objectspy.util;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class KeyUtils {
    public static boolean isModifier(int keycode) {
        return keycode == KeyEvent.VK_CONTROL || keycode == KeyEvent.VK_SHIFT || keycode == KeyEvent.VK_ALT
                || keycode == KeyEvent.VK_META;
    }

    /**
     * Returns a String describing the keyCode, such as "HOME", "F1" or "A".
     * These strings can be localized by changing the awt.properties file.
     *
     * @return a string containing a text description for a physical key,
     * identified by its keyCode
     */
    public static String getKeyText(int keyCode) {
        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9
                || keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return String.valueOf((char) keyCode);
        }

        switch (keyCode) {
            case KeyEvent.VK_ENTER:
                return Toolkit.getProperty("AWT.enter", "Enter");
            case KeyEvent.VK_BACK_SPACE:
                return Toolkit.getProperty("AWT.backSpace", "Backspace");
            case KeyEvent.VK_TAB:
                return Toolkit.getProperty("AWT.tab", "Tab");
            case KeyEvent.VK_CANCEL:
                return Toolkit.getProperty("AWT.cancel", "Cancel");
            case KeyEvent.VK_CLEAR:
                return Toolkit.getProperty("AWT.clear", "Clear");
            case KeyEvent.VK_COMPOSE:
                return Toolkit.getProperty("AWT.compose", "Compose");
            case KeyEvent.VK_PAUSE:
                return Toolkit.getProperty("AWT.pause", "Pause");
            case KeyEvent.VK_CAPS_LOCK:
                return Toolkit.getProperty("AWT.capsLock", "Caps Lock");
            case KeyEvent.VK_ESCAPE:
                return Toolkit.getProperty("AWT.escape", "Escape");
            case KeyEvent.VK_SPACE:
                return Toolkit.getProperty("AWT.space", "Space");
            case KeyEvent.VK_PAGE_UP:
                return Toolkit.getProperty("AWT.pgup", "Page Up");
            case KeyEvent.VK_PAGE_DOWN:
                return Toolkit.getProperty("AWT.pgdn", "Page Down");
            case KeyEvent.VK_END:
                return Toolkit.getProperty("AWT.end", "End");
            case KeyEvent.VK_HOME:
                return Toolkit.getProperty("AWT.home", "Home");
            case KeyEvent.VK_LEFT:
                return Toolkit.getProperty("AWT.left", "Left");
            case KeyEvent.VK_UP:
                return Toolkit.getProperty("AWT.up", "Up");
            case KeyEvent.VK_RIGHT:
                return Toolkit.getProperty("AWT.right", "Right");
            case KeyEvent.VK_DOWN:
                return Toolkit.getProperty("AWT.down", "Down");
            case KeyEvent.VK_BEGIN:
                return Toolkit.getProperty("AWT.begin", "Begin");

            // modifiers
            case KeyEvent.VK_SHIFT:
                return Toolkit.getProperty("AWT.shift", "Shift");
            case KeyEvent.VK_CONTROL:
                return Toolkit.getProperty("AWT.control", "Control");
            case KeyEvent.VK_ALT:
                return Toolkit.getProperty("AWT.alt", "Alt");
            case KeyEvent.VK_META:
                return Toolkit.getProperty("AWT.meta", "Meta");
            case KeyEvent.VK_ALT_GRAPH:
                return Toolkit.getProperty("AWT.altGraph", "Alt Graph");

            // punctuation
            case KeyEvent.VK_COMMA:
                return ",";
            case KeyEvent.VK_PERIOD:
                return ".";
            case KeyEvent.VK_SLASH:
                return "/";
            case KeyEvent.VK_SEMICOLON:
                return ";";
            case KeyEvent.VK_EQUALS:
                return "=";
            case KeyEvent.VK_OPEN_BRACKET:
                return "(";
            case KeyEvent.VK_BACK_SLASH:
                return "\\";
            case KeyEvent.VK_CLOSE_BRACKET:
                return ")";

            // numpad numeric keys handled below
            case KeyEvent.VK_MULTIPLY:
                return Toolkit.getProperty("AWT.multiply", "NumPad *");
            case KeyEvent.VK_ADD:
                return Toolkit.getProperty("AWT.add", "NumPad +");
            case KeyEvent.VK_SEPARATOR:
                return Toolkit.getProperty("AWT.separator", "NumPad ,");
            case KeyEvent.VK_SUBTRACT:
                return Toolkit.getProperty("AWT.subtract", "NumPad -");
            case KeyEvent.VK_DECIMAL:
                return Toolkit.getProperty("AWT.decimal", "NumPad .");
            case KeyEvent.VK_DIVIDE:
                return Toolkit.getProperty("AWT.divide", "NumPad /");
            case KeyEvent.VK_DELETE:
                return Toolkit.getProperty("AWT.delete", "Delete");
            case KeyEvent.VK_NUM_LOCK:
                return Toolkit.getProperty("AWT.numLock", "Num Lock");
            case KeyEvent.VK_SCROLL_LOCK:
                return Toolkit.getProperty("AWT.scrollLock", "Scroll Lock");

            case KeyEvent.VK_WINDOWS:
                return Toolkit.getProperty("AWT.windows", "Windows");
            case KeyEvent.VK_CONTEXT_MENU:
                return Toolkit.getProperty("AWT.context", "Context Menu");

            case KeyEvent.VK_F1:
                return Toolkit.getProperty("AWT.f1", "F1");
            case KeyEvent.VK_F2:
                return Toolkit.getProperty("AWT.f2", "F2");
            case KeyEvent.VK_F3:
                return Toolkit.getProperty("AWT.f3", "F3");
            case KeyEvent.VK_F4:
                return Toolkit.getProperty("AWT.f4", "F4");
            case KeyEvent.VK_F5:
                return Toolkit.getProperty("AWT.f5", "F5");
            case KeyEvent.VK_F6:
                return Toolkit.getProperty("AWT.f6", "F6");
            case KeyEvent.VK_F7:
                return Toolkit.getProperty("AWT.f7", "F7");
            case KeyEvent.VK_F8:
                return Toolkit.getProperty("AWT.f8", "F8");
            case KeyEvent.VK_F9:
                return Toolkit.getProperty("AWT.f9", "F9");
            case KeyEvent.VK_F10:
                return Toolkit.getProperty("AWT.f10", "F10");
            case KeyEvent.VK_F11:
                return Toolkit.getProperty("AWT.f11", "F11");
            case KeyEvent.VK_F12:
                return Toolkit.getProperty("AWT.f12", "F12");
            case KeyEvent.VK_F13:
                return Toolkit.getProperty("AWT.f13", "F13");
            case KeyEvent.VK_F14:
                return Toolkit.getProperty("AWT.f14", "F14");
            case KeyEvent.VK_F15:
                return Toolkit.getProperty("AWT.f15", "F15");
            case KeyEvent.VK_F16:
                return Toolkit.getProperty("AWT.f16", "F16");
            case KeyEvent.VK_F17:
                return Toolkit.getProperty("AWT.f17", "F17");
            case KeyEvent.VK_F18:
                return Toolkit.getProperty("AWT.f18", "F18");
            case KeyEvent.VK_F19:
                return Toolkit.getProperty("AWT.f19", "F19");
            case KeyEvent.VK_F20:
                return Toolkit.getProperty("AWT.f20", "F20");
            case KeyEvent.VK_F21:
                return Toolkit.getProperty("AWT.f21", "F21");
            case KeyEvent.VK_F22:
                return Toolkit.getProperty("AWT.f22", "F22");
            case KeyEvent.VK_F23:
                return Toolkit.getProperty("AWT.f23", "F23");
            case KeyEvent.VK_F24:
                return Toolkit.getProperty("AWT.f24", "F24");

            case KeyEvent.VK_PRINTSCREEN:
                return Toolkit.getProperty("AWT.printScreen", "Print Screen");
            case KeyEvent.VK_INSERT:
                return Toolkit.getProperty("AWT.insert", "Insert");
            case KeyEvent.VK_HELP:
                return Toolkit.getProperty("AWT.help", "Help");
            case KeyEvent.VK_BACK_QUOTE:
                return "`";
            case KeyEvent.VK_QUOTE:
                return "'";

            case KeyEvent.VK_KP_UP:
                return Toolkit.getProperty("AWT.up", "Up");
            case KeyEvent.VK_KP_DOWN:
                return Toolkit.getProperty("AWT.down", "Down");
            case KeyEvent.VK_KP_LEFT:
                return Toolkit.getProperty("AWT.left", "Left");
            case KeyEvent.VK_KP_RIGHT:
                return Toolkit.getProperty("AWT.right", "Right");

            case KeyEvent.VK_DEAD_GRAVE:
                return Toolkit.getProperty("AWT.deadGrave", "Dead Grave");
            case KeyEvent.VK_DEAD_ACUTE:
                return Toolkit.getProperty("AWT.deadAcute", "Dead Acute");
            case KeyEvent.VK_DEAD_CIRCUMFLEX:
                return Toolkit.getProperty("AWT.deadCircumflex", "Dead Circumflex");
            case KeyEvent.VK_DEAD_TILDE:
                return Toolkit.getProperty("AWT.deadTilde", "Dead Tilde");
            case KeyEvent.VK_DEAD_MACRON:
                return Toolkit.getProperty("AWT.deadMacron", "Dead Macron");
            case KeyEvent.VK_DEAD_BREVE:
                return Toolkit.getProperty("AWT.deadBreve", "Dead Breve");
            case KeyEvent.VK_DEAD_ABOVEDOT:
                return Toolkit.getProperty("AWT.deadAboveDot", "Dead Above Dot");
            case KeyEvent.VK_DEAD_DIAERESIS:
                return Toolkit.getProperty("AWT.deadDiaeresis", "Dead Diaeresis");
            case KeyEvent.VK_DEAD_ABOVERING:
                return Toolkit.getProperty("AWT.deadAboveRing", "Dead Above Ring");
            case KeyEvent.VK_DEAD_DOUBLEACUTE:
                return Toolkit.getProperty("AWT.deadDoubleAcute", "Dead Double Acute");
            case KeyEvent.VK_DEAD_CARON:
                return Toolkit.getProperty("AWT.deadCaron", "Dead Caron");
            case KeyEvent.VK_DEAD_CEDILLA:
                return Toolkit.getProperty("AWT.deadCedilla", "Dead Cedilla");
            case KeyEvent.VK_DEAD_OGONEK:
                return Toolkit.getProperty("AWT.deadOgonek", "Dead Ogonek");
            case KeyEvent.VK_DEAD_IOTA:
                return Toolkit.getProperty("AWT.deadIota", "Dead Iota");
            case KeyEvent.VK_DEAD_VOICED_SOUND:
                return Toolkit.getProperty("AWT.deadVoicedSound", "Dead Voiced Sound");
            case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
                return Toolkit.getProperty("AWT.deadSemivoicedSound", "Dead Semivoiced Sound");

            case KeyEvent.VK_AMPERSAND:
                return Toolkit.getProperty("AWT.ampersand", "Ampersand");
            case KeyEvent.VK_ASTERISK:
                return Toolkit.getProperty("AWT.asterisk", "Asterisk");
            case KeyEvent.VK_QUOTEDBL:
                return Toolkit.getProperty("AWT.quoteDbl", "Double Quote");
            case KeyEvent.VK_LESS:
                return Toolkit.getProperty("AWT.Less", "Less");
            case KeyEvent.VK_GREATER:
                return Toolkit.getProperty("AWT.greater", "Greater");
            case KeyEvent.VK_BRACELEFT:
                return Toolkit.getProperty("AWT.braceLeft", "Left Brace");
            case KeyEvent.VK_BRACERIGHT:
                return Toolkit.getProperty("AWT.braceRight", "Right Brace");
            case KeyEvent.VK_AT:
                return Toolkit.getProperty("AWT.at", "At");
            case KeyEvent.VK_COLON:
                return Toolkit.getProperty("AWT.colon", "Colon");
            case KeyEvent.VK_CIRCUMFLEX:
                return Toolkit.getProperty("AWT.circumflex", "Circumflex");
            case KeyEvent.VK_DOLLAR:
                return Toolkit.getProperty("AWT.dollar", "Dollar");
            case KeyEvent.VK_EURO_SIGN:
                return Toolkit.getProperty("AWT.euro", "Euro");
            case KeyEvent.VK_EXCLAMATION_MARK:
                return "!";
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                return Toolkit.getProperty("AWT.invertedExclamationMark", "Inverted Exclamation Mark");
            case KeyEvent.VK_LEFT_PARENTHESIS:
                return "(";
            case KeyEvent.VK_NUMBER_SIGN:
                return "!";
            case KeyEvent.VK_MINUS:
                return "-";
            case KeyEvent.VK_PLUS:
                return "+";
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                return ")";
            case KeyEvent.VK_UNDERSCORE:
                return "_";

            case KeyEvent.VK_FINAL:
                return Toolkit.getProperty("AWT.final", "Final");
            case KeyEvent.VK_CONVERT:
                return Toolkit.getProperty("AWT.convert", "Convert");
            case KeyEvent.VK_NONCONVERT:
                return Toolkit.getProperty("AWT.noconvert", "No Convert");
            case KeyEvent.VK_ACCEPT:
                return Toolkit.getProperty("AWT.accept", "Accept");
            case KeyEvent.VK_MODECHANGE:
                return Toolkit.getProperty("AWT.modechange", "Mode Change");
            case KeyEvent.VK_KANA:
                return Toolkit.getProperty("AWT.kana", "Kana");
            case KeyEvent.VK_KANJI:
                return Toolkit.getProperty("AWT.kanji", "Kanji");
            case KeyEvent.VK_ALPHANUMERIC:
                return Toolkit.getProperty("AWT.alphanumeric", "Alphanumeric");
            case KeyEvent.VK_KATAKANA:
                return Toolkit.getProperty("AWT.katakana", "Katakana");
            case KeyEvent.VK_HIRAGANA:
                return Toolkit.getProperty("AWT.hiragana", "Hiragana");
            case KeyEvent.VK_FULL_WIDTH:
                return Toolkit.getProperty("AWT.fullWidth", "Full-Width");
            case KeyEvent.VK_HALF_WIDTH:
                return Toolkit.getProperty("AWT.halfWidth", "Half-Width");
            case KeyEvent.VK_ROMAN_CHARACTERS:
                return Toolkit.getProperty("AWT.romanCharacters", "Roman Characters");
            case KeyEvent.VK_ALL_CANDIDATES:
                return Toolkit.getProperty("AWT.allCandidates", "All Candidates");
            case KeyEvent.VK_PREVIOUS_CANDIDATE:
                return Toolkit.getProperty("AWT.previousCandidate", "Previous Candidate");
            case KeyEvent.VK_CODE_INPUT:
                return Toolkit.getProperty("AWT.codeInput", "Code Input");
            case KeyEvent.VK_JAPANESE_KATAKANA:
                return Toolkit.getProperty("AWT.japaneseKatakana", "Japanese Katakana");
            case KeyEvent.VK_JAPANESE_HIRAGANA:
                return Toolkit.getProperty("AWT.japaneseHiragana", "Japanese Hiragana");
            case KeyEvent.VK_JAPANESE_ROMAN:
                return Toolkit.getProperty("AWT.japaneseRoman", "Japanese Roman");
            case KeyEvent.VK_KANA_LOCK:
                return Toolkit.getProperty("AWT.kanaLock", "Kana Lock");
            case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                return Toolkit.getProperty("AWT.inputMethodOnOff", "Input Method On/Off");

            case KeyEvent.VK_AGAIN:
                return Toolkit.getProperty("AWT.again", "Again");
            case KeyEvent.VK_UNDO:
                return Toolkit.getProperty("AWT.undo", "Undo");
            case KeyEvent.VK_COPY:
                return Toolkit.getProperty("AWT.copy", "Copy");
            case KeyEvent.VK_PASTE:
                return Toolkit.getProperty("AWT.paste", "Paste");
            case KeyEvent.VK_CUT:
                return Toolkit.getProperty("AWT.cut", "Cut");
            case KeyEvent.VK_FIND:
                return Toolkit.getProperty("AWT.find", "Find");
            case KeyEvent.VK_PROPS:
                return Toolkit.getProperty("AWT.props", "Props");
            case KeyEvent.VK_STOP:
                return Toolkit.getProperty("AWT.stop", "Stop");
        }

        if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
            String numpad = Toolkit.getProperty("AWT.numpad", "NumPad");
            char c = (char) (keyCode - KeyEvent.VK_NUMPAD0 + '0');
            return numpad + "-" + c;
        }

        if ((keyCode & 0x01000000) != 0) {
            return String.valueOf((char) (keyCode ^ 0x01000000));
        }
        String unknown = Toolkit.getProperty("AWT.unknown", "Unknown");
        return unknown + " keyCode: 0x" + Integer.toString(keyCode, 16);
    }
}
