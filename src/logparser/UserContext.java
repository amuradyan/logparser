package logparser;

/**
 * Created by spectrum on 11/28/2017.
 */
class UserContext {
  private String left;
  private String right;

  public UserContext(String left, String right) {
    this.left = left;
    this.right = right;
  }

  public UserContext(){}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserContext that = (UserContext) o;

    if (left != null ? !left.equals(that.left) : that.left != null) return false;
    return right != null ? right.equals(that.right) : that.right == null;
  }

  @Override
  public int hashCode() {
    int result = left != null ? left.hashCode() : 0;
    result = 31 * result + (right != null ? right.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "UserContext{" +
        "left='" + left + '\'' +
        ", right='" + right + '\'' +
        '}';
  }
}
