package com.example.community;

import java.util.List;

public class WarningResponse {

    private Metadata metadata;
    private List<Warning> results;

    // Getters and Setters
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Warning> getResults() {
        return results;
    }

    public void setResults(List<Warning> results) {
        this.results = results;
    }

    public static class Metadata {
        private Resultset resultset;

        // Getter and Setter
        public Resultset getResultset() {
            return resultset;
        }

        public void setResultset(Resultset resultset) {
            this.resultset = resultset;
        }
    }

    public static class Resultset {
        private int count;

        // Getter and Setter
        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class Warning {
        private String date;
        private String datatype;
        private Value value;
        private Attributes attributes; // Add Attributes class here

        // Getter and Setter for date
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        // Getter and Setter for datatype
        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        // Getter and Setter for value
        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }

        // Getter and Setter for attributes
        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public static class Value {
            private Heading heading;
            private Text text;

            // Getter and Setter for heading
            public Heading getHeading() {
                return heading;
            }

            public void setHeading(Heading heading) {
                this.heading = heading;
            }

            // Getter and Setter for text
            public Text getText() {
                return text;
            }

            public void setText(Text text) {
                this.text = text;
            }

            public static class Heading {
                private String en;
                private String ms;

                // Getter and Setter for heading (English and Malay)
                public String getEn() {
                    return en;
                }

                public void setEn(String en) {
                    this.en = en;
                }

                public String getMs() {
                    return ms;
                }

                public void setMs(String ms) {
                    this.ms = ms;
                }
            }

            public static class Text {
                private WarningDetails en;  // English warning message
                private WarningDetails ms;  // Malay warning message

                // Getter and Setter for English warning message
                public WarningDetails getEn() {
                    return en;
                }

                public void setEn(WarningDetails en) {
                    this.en = en;
                }

                // Getter and Setter for Malay warning message
                public WarningDetails getMs() {
                    return ms;
                }

                public void setMs(WarningDetails ms) {
                    this.ms = ms;
                }

                // Nested class to hold the warning details (warning message, subtitle, area)
                public static class WarningDetails {
                    private String warning;   // The actual warning message
                    private String subtitle;  // The subtitle (if available)
                    private String area;      // The area (if available)

                    // Getter and Setter for warning message
                    public String getWarning() {
                        return warning;
                    }

                    public void setWarning(String warning) {
                        this.warning = warning;
                    }

                    // Getter and Setter for subtitle
                    public String getSubtitle() {
                        return subtitle;
                    }

                    public void setSubtitle(String subtitle) {
                        this.subtitle = subtitle;
                    }

                    // Getter and Setter for area
                    public String getArea() {
                        return area;
                    }

                    public void setArea(String area) {
                        this.area = area;
                    }
                }
            }
        }

        // New Attributes class to hold the title, ref, timestamp, valid_from, and valid_to
        public static class Attributes {
            private Title title;
            private String ref;
            private String timestamp;
            private String valid_from;
            private String valid_to;

            // Getter and Setter for title
            public Title getTitle() {
                return title;
            }

            public void setTitle(Title title) {
                this.title = title;
            }

            // Getter and Setter for ref
            public String getRef() {
                return ref;
            }

            public void setRef(String ref) {
                this.ref = ref;
            }

            // Getter and Setter for timestamp
            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            // Getter and Setter for valid_from
            public String getValid_from() {
                return valid_from;
            }

            public void setValid_from(String valid_from) {
                this.valid_from = valid_from;
            }

            // Getter and Setter for valid_to
            public String getValid_to() {
                return valid_to;
            }

            public void setValid_to(String valid_to) {
                this.valid_to = valid_to;
            }

            public static class Title {
                private String en;
                private String ms;

                // Getter and Setter for title (English and Malay)
                public String getEn() {
                    return en;
                }

                public void setEn(String en) {
                    this.en = en;
                }

                public String getMs() {
                    return ms;
                }

                public void setMs(String ms) {
                    this.ms = ms;
                }
            }
        }
    }
}






