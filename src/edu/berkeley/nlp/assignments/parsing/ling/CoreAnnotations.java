package edu.berkeley.nlp.assignments.parsing.ling;

import edu.berkeley.nlp.assignments.parsing.util.CoreMap;
import edu.berkeley.nlp.assignments.parsing.util.ErasureUtils;

import java.util.List;
import java.util.Map;


public class CoreAnnotations {

  private CoreAnnotations() { } // only static members

  
  public static class TextAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class LemmaAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class PartOfSpeechAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class NamedEntityTagAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class NamedEntityTagProbsAnnotation implements CoreAnnotation<Map<String,Double>> {
    @Override
    public Class<Map<String,Double>> getType() {
      return ErasureUtils.uncheckedCast(Map.class);
    }
  }

  
  public static class StackedNamedEntityTagAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class TokensAnnotation implements CoreAnnotation<List<CoreLabel>> {
    @Override
    public Class<List<CoreLabel>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }

  
  public static class SentencesAnnotation implements CoreAnnotation<List<CoreMap>> {
    @Override
    public Class<List<CoreMap>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }

  
  public static class TokenBeginAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class TokenEndAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class DocIDAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class IndexAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class BeginIndexAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class EndIndexAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class ForcedSentenceUntilEndAnnotation
          implements CoreAnnotation<Boolean> {
    @Override
    public Class<Boolean> getType() {
      return Boolean.class;
    }
  }

  
  public static class ForcedSentenceEndAnnotation
  implements CoreAnnotation<Boolean> {
    @Override
    public Class<Boolean> getType() {
      return Boolean.class;
    }
  }

  
  public static class SentenceIndexAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class ValueAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class CategoryAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class OriginalTextAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class BeforeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class AfterAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  /**
   * CoNLL SRL/dep parsing - whether the word is a predicate
   */
  public static class CoNLLPredicateAnnotation implements CoreAnnotation<Boolean> {
    @Override
    public Class<Boolean> getType() {
      return Boolean.class;
    }
  }

  /**
   * CoNLL SRL/dep parsing - map which, for the current word, specifies its
   * specific role for each predicate
   */
  public static class CoNLLSRLAnnotation implements CoreAnnotation<Map<Integer,String>> {
    @Override
    public Class<Map<Integer,String>> getType() {
      return ErasureUtils.uncheckedCast(Map.class);
    }
  }

  
  public static class CoNLLDepTypeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class CoNLLDepParentIndexAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class IDFAnnotation implements CoreAnnotation<Double> {
    @Override
    public Class<Double> getType() {
      return Double.class;
    }
  }

  
  public static class ArgumentAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class MarkingAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class SemanticHeadWordAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class SemanticHeadTagAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class VerbSenseAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class CategoryFunctionalTagAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class NormalizedNamedEntityTagAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public enum SRL_ID {
    ARG, NO, ALL_NO, REL
  }


  
  public static class ShapeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class LeftTermAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class ParentAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class AnswerAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class GoldAnswerAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class FeaturesAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class InterpretationAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class RoleAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class GazetteerAnnotation implements CoreAnnotation<List<String>> {
    @Override
    public Class<List<String>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }

  
  public static class StemAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class PolarityAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class MorphoNumAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class MorphoPersAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class MorphoGenAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class MorphoCaseAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class ChineseCharAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  /** For Chinese: the segmentation info existing in the original text. */
  public static class ChineseOrigSegAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  /** For Chinese: the segmentation information from the segmenter.
   *  Either a "1" for a new word starting at this position or a "0".
   */
  public static class ChineseSegAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class CharacterOffsetBeginAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class CharacterOffsetEndAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  
  public static class TagLabelAnnotation implements CoreAnnotation<Label> {
    @Override
    public Class<Label> getType() {
      return Label.class;
    }
  }

  /** Possibly this should be grouped with gazetteer annotation - original key
   *  was "gaz".
   */
  public static class GazAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class AbbrAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class ChunkAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class GovernorAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class AbstrAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class FreqAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class WebAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  public static class LinkAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class SectionAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class SectionDateAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class SectionIDAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  // Why do both this and sentenceposannotation exist? I don't know, but one
  // class
  // uses both so here they remain for now...
  public static class SentenceIDAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class HeadWordStringAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  // gets the synonymn of a word in the Wordnet (use a bit differently in sonalg's code)
  public static class WordnetSynAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  //to get prototype feature, see Haghighi Exemplar driven learning
  public static class ProtoAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  // Document date
  // Needed by SUTime
  public static class DocDateAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class DocTypeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class DocSourceTypeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class DocTitleAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class LocationAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  
  public static class AuthorAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  /** Annotation indicating whether the numeric phrase the token is part of
   * represents a NUMBER or ORDINAL (twenty first {@literal =>} ORDINAL ORDINAL).
   */
  public static class NumericCompositeValueAnnotation implements CoreAnnotation<Number> {
    @Override
    public Class<Number> getType() {
      return Number.class;
    }
  }

  /** Annotation indicating the numeric value of the phrase the token is part of
   * (twenty first {@literal =>} 21 21 ).
   */
  public static class NumericCompositeTypeAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class NumerizedTokensAnnotation implements CoreAnnotation<List<CoreMap>> {
    @Override
    public Class<List<CoreMap>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }

  
  public static class SpeakerAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }


  
  public static class MentionTokenAnnotation implements CoreAnnotation<MultiTokenTag> {
    @Override
    public Class<MultiTokenTag> getType() {
      return MultiTokenTag.class;
    }
  }


  
  public static class IsNewlineAnnotation implements CoreAnnotation<Boolean> {
    @Override
    public Class<Boolean> getType() {return Boolean.class;}
  }



}
