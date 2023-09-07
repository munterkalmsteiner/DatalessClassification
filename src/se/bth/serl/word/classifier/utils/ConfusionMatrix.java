package se.bth.serl.word.classifier.utils;

public class ConfusionMatrix {
	private Integer tp;
	private Integer tn;
	private Integer fp;
	private Integer fn;
	
	public ConfusionMatrix (){
		tp = 0;
		tn = 0;
		fp = 0;
		fn = 0;
	}
	public Integer getTp() {
		return tp;
	}
	public void setTp(Integer tp) {
		this.tp = tp;
	}
	public Integer getTn() {
		return tn;
	}
	public void setTn(Integer tn) {
		this.tn = tn;
	}
	public Integer getFp() {
		return fp;
	}
	public void setFp(Integer fp) {
		this.fp = fp;
	}
	public Integer getFn() {
		return fn;
	}
	public void setFn(Integer fn) {
		this.fn = fn;
	}
}
